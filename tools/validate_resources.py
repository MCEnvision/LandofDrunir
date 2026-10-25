import json
import re
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = [ROOT / 'src/main/resources', ROOT / 'src/generated/resources']


def main():
    content = json.loads((RESOURCES[0] / 'assets/landofdrunir/content.json').read_text())
    errors = []
    models = {}
    block_kinds = {'cube', 'bottom_top', 'plant', 'leaves', 'log', 'hollow', 'door', 'trapdoor',
                   'shore', 'boulder', 'boulder_piece', 'slab', 'stairs'}
    item_kinds = {'item', 'food', 'sword', 'lance', 'axe', 'pickaxe', 'shovel', 'hoe', 'armor'}
    for group, kinds in (('blocks', block_kinds), ('items', item_kinds)):
        for entry in content[group]:
            if not re.fullmatch(r'[a-z0-9_]+', entry['name']) or entry['kind'] not in kinds:
                errors.append(f'Invalid {group} catalog entry: {entry}')
    if len(content['creatures']) != len(set(content['creatures'])) or 'deer' not in content['creatures']:
        errors.append('Creature identifiers must be unique and include deer.')
    if len(content['textures']) != len(set(content['textures'])):
        errors.append('Texture sample identifiers must be unique.')
    for resource in RESOURCES:
        for path in resource.rglob('*.json'):
            try:
                data = json.loads(path.read_text())
            except (ValueError, UnicodeError) as error:
                errors.append(f'{path}: {error}')
                continue
            relative = path.relative_to(resource).as_posix()
            if relative.startswith('assets/landofdrunir/models/'):
                identifier = 'landofdrunir:' + relative.split('/models/', 1)[1][:-5]
                if identifier in models:
                    errors.append(f'Duplicate model resource: {identifier}')
                models[identifier] = data
            if relative.startswith('assets/landofdrunir/blockstates/'):
                for variant in data.get('variants', {}).values():
                    for value in variant if isinstance(variant, list) else [variant]:
                        target = value['model'].split(':', 1)[1]
                        if not any((base / ('assets/landofdrunir/models/' + target + '.json')).is_file() for base in RESOURCES):
                            errors.append(f'{path}: missing model {target}')
    for identifier, model in models.items():
        parent = model.get('parent', '')
        if parent.startswith('landofdrunir:') and parent not in models:
            errors.append(f'{identifier}: missing parent {parent}')
        visited = {identifier}
        lineage = [model]
        while parent.startswith('landofdrunir:') and parent in models:
            if parent in visited:
                errors.append(f'{identifier}: cyclic parent {parent}')
                break
            visited.add(parent)
            lineage.append(models[parent])
            parent = models[parent].get('parent', '')
        textures = {}
        for ancestor in reversed(lineage):
            textures.update(ancestor.get('textures', {}))
        for face in (face for element in model.get('elements', []) for face in element.get('faces', {}).values()):
            reference = face.get('texture', '')
            aliases = set()
            while reference.startswith('#'):
                if reference in aliases or reference[1:] not in textures:
                    errors.append(f'{identifier}: unresolved or cyclic texture alias {reference}')
                    break
                aliases.add(reference)
                reference = textures[reference[1:]]
        for texture in model.get('textures', {}).values():
            if texture.startswith('landofdrunir:'):
                target = texture.split(':', 1)[1]
                if not (RESOURCES[0] / ('assets/landofdrunir/textures/' + target + '.png')).is_file():
                    errors.append(f'{identifier}: missing texture {texture}')
    for group in ('blocks', 'items'):
        names = [entry['name'] for entry in content[group]]
        if len(names) != len(set(names)):
            errors.append(f'Duplicate {group} identifiers')
        for name in names:
            if 'landofdrunir:item/' + name not in models:
                errors.append(f'Missing item model for {name}')
    registered_items = [entry['name'] for group in ('blocks', 'items') for entry in content[group]]
    registered_items += [name + '_spawn_egg' for name in content['creatures']]
    registered_items += ['texture/' + name for name in content['textures']]
    catalog = json.loads((RESOURCES[0] / 'assets/landofdrunir/boulders.json').read_text())
    registered_items += [material['id'] + '_' + size['id'] + '_' + form['id'] + '_boulder'
                         for material in catalog['materials'] for size in catalog['sizes'] for form in catalog['forms']]
    if len(registered_items) != len(set(registered_items)):
        errors.append('Block items, catalog items, eggs, gallery samples, and boulder items must have distinct registry identifiers.')
    language = json.loads((RESOURCES[1] / 'assets/landofdrunir/lang/en_us.json').read_text())
    for name in [*content['creatures'], 'magmafiend_fireball']:
        if not language.get('entity.landofdrunir.' + name):
            errors.append(f'Missing entity translation: {name}')
    for block in content['blocks']:
        name = block['name']
        enabled = block.get('random_y_rotation', False)
        if not isinstance(enabled, bool):
            errors.append(f'{name}: random_y_rotation must be a boolean')
        state = json.loads((RESOURCES[1] / f'assets/landofdrunir/blockstates/{name}.json').read_text())
        variants = state['variants']
        if enabled:
            choices = variants.get('')
            if block['kind'] not in ('cube', 'bottom_top', 'leaves', 'plant'):
                errors.append(f'{name}: random rotation cannot replace directional block states')
            if set(variants) != {''} or not isinstance(choices, list) or len(choices) != 4:
                errors.append(f'{name}: expected four natural rotation choices')
                continue
            if {choice.get('y', 0) for choice in choices} != {0, 90, 180, 270}:
                errors.append(f'{name}: natural rotations must cover all four horizontal directions')
            for choice in choices:
                if choice.get('x', 0) != 0 or choice.get('uvlock', False):
                    errors.append(f'{name}: natural textures must rotate horizontally without UV locking')
                if choice.get('weight', 1) != 1 or choice.get('model') != f'landofdrunir:block/{name}':
                    errors.append(f'{name}: natural rotations must use the same model with equal weights')
        elif block['kind'] != 'shore' and any(isinstance(choice, list) for choice in variants.values()):
            errors.append(f'{name}: unexpected random model choices on a fixed block')
    expected = set(content['textures'])
    actual = set()
    for path in (RESOURCES[0] / 'assets/landofdrunir/textures').rglob('*.png'):
        data = path.read_bytes()
        if data[:8] != b'\x89PNG\r\n\x1a\n' or len(data) < 24:
            errors.append(f'Invalid PNG: {path}')
            continue
        width, height = struct.unpack('>II', data[16:24])
        if width == 0 or height == 0:
            errors.append(f'Empty texture: {path}')
        name = path.relative_to(RESOURCES[0] / 'assets/landofdrunir/textures').with_suffix('').as_posix()
        actual.add(name)
        if 'landofdrunir:item/texture/' + name not in models:
            errors.append(f'Missing texture sample: {name}')
    if actual != expected:
        errors.append(f'Texture catalog mismatch: missing={expected-actual}, extra={actual-expected}')
    atlas = json.loads((RESOURCES[1] / 'assets/minecraft/atlases/blocks.json').read_text())
    stitched = {entry['resource'] for entry in atlas['sources'] if entry['type'] == 'minecraft:single'}
    palettes = {value for entry in atlas['sources'] for value in entry.get('permutations', {}).values()}
    for name in expected:
        if not name.startswith(('block/', 'item/', 'trims/')) and 'landofdrunir:' + name not in stitched:
            errors.append(f'Texture is absent from the item atlas: {name}')
        if name.startswith('trims/') and 'landofdrunir:' + name not in palettes:
            errors.append(f'Trim palette is absent from atlas permutations: {name}')
    if errors:
        raise SystemExit('\n'.join(errors))
    worldgen = RESOURCES[1] / 'data/landofdrunir/worldgen'
    tree = json.loads((worldgen / 'configured_feature/golden_birch.json').read_text())
    biome = json.loads((worldgen / 'biome/yellow_tree_forest.json').read_text())
    placed = json.loads((worldgen / 'placed_feature/golden_birch_trees.json').read_text())
    if tree['config']['trunk_provider']['state']['Name'] != 'minecraft:birch_log':
        raise SystemExit('Yellow Tree Forest trees must use vanilla birch logs.')
    if tree['config']['foliage_provider']['state']['Name'] != 'landofdrunir:yellow_birch_leaves':
        raise SystemExit('Yellow Tree Forest trees must use custom yellow birch leaves.')
    if tree['config']['dirt_provider']['state']['Name'] != 'minecraft:dirt':
        raise SystemExit('Yellow Tree Forest trees must retain regular dirt.')
    if placed['feature'] != 'landofdrunir:golden_birch_grove' or 'landofdrunir:golden_birch_trees' not in biome['features'][9]:
        raise SystemExit('Yellow Tree Forest tree generation is disconnected.')
    print(f'Validated {len(models)} models and complete coverage of {len(actual)} PNG textures.')

    for biome_name in ('yellow_tree_forest', 'golden_birch_island', 'golden_birch_shore', 'golden_birch_waters'):
        data = json.loads((worldgen / f'biome/{biome_name}.json').read_text())
        features = {entry for step in data['features'] for entry in step}
        if data['carvers'] or any(data['spawners'].values()):
            raise SystemExit(f'{biome_name}: caves and natural mob lists must be empty.')
        vegetation = set(data['features'][9])
        allowed = {'landofdrunir:golden_birch_trees', 'landofdrunir:sun_holly_patch', 'landofdrunir:island_boulders', 'landofdrunir:island_outcrops', 'landofdrunir:sunken_ships'}
        aquatic = ('minecraft:warm_ocean_vegetation', 'minecraft:seagrass_warm', 'minecraft:sea_pickle')
        if biome_name not in ('yellow_tree_forest', 'golden_birch_island'):
            allowed.update(aquatic)
            expected_order = [*aquatic, 'landofdrunir:sunken_ships']
            if data['features'][9] != expected_order:
                raise SystemExit(f'{biome_name}: reefs and seagrass must precede shipwrecks in vanilla order.')
        if not vegetation <= allowed:
            raise SystemExit(f'{biome_name}: unexpected vegetation or structure feature.')
        if data['features'][1] or data['features'][3] or data['features'][8]:
            raise SystemExit(f'{biome_name}: lakes, underground structures and springs must be absent.')
        if any('patch_grass' in name or 'patch_tall_grass' in name for name in features):
            raise SystemExit(f'{biome_name}: natural green grass generation is forbidden.')
        if 'landofdrunir:shore_blend' not in features:
            raise SystemExit(f'{biome_name}: shoreline blending is disconnected.')
    for block in content['blocks']:
        name = block['name']
        if block['kind'] == 'boulder':
            model = models['landofdrunir:block/' + name]
            if max(element['to'][1] for element in model['elements']) != 8:
                raise SystemExit(f'{name}: boulders must fit in a half block.')
            expected = {f'facing={facing},type={kind}' for facing in ('north', 'east', 'south', 'west')
                        for kind in ('bottom', 'top', 'double')}
            state = json.loads((RESOURCES[1] / f'assets/landofdrunir/blockstates/{name}.json').read_text())
            if set(state['variants']) != expected:
                raise SystemExit(f'{name}: missing directional or stacked models.')
        if block['kind'] == 'shore':
            state = json.loads((RESOURCES[1] / f'assets/landofdrunir/blockstates/{name}.json').read_text())
            if set(state['variants']) != {f'tone={tone}' for tone in range(8)}:
                raise SystemExit('The shoreline must expose all eight palette tones.')

    for box in content['boulder_shape']:
        if len(box) != 6 or any(not (0 <= box[i] < box[i + 3] <= (8 if i == 1 else 16)) for i in range(3)):
            raise SystemExit('Invalid boulder geometry bounds.')

    from boulder_resources import block_id, item_id, geometry
    boulders = json.loads((RESOURCES[0] / 'assets/landofdrunir/boulders.json').read_text())
    if len(boulders['forms']) != 4 or len(boulders['sizes']) != 5 or len(boulders['widths']) != 8:
        raise SystemExit('Boulders require four forms, five sizes, and eight layer widths.')
    for material in boulders['materials']:
        name = block_id(material['id'])
        states = json.loads((RESOURCES[1] / f'assets/landofdrunir/blockstates/{name}.json').read_text())['variants']
        connected = models['landofdrunir:block/' + name]
        if connected.get('loader') != 'landofdrunir:connected_boulder' or connected.get('fallback') not in models:
            raise SystemExit(f'{name}: missing connected loader or standalone fallback.')
        if any(state.get('model') != 'landofdrunir:block/' + name or state.get('y', 0) != 0
               for state in states.values()):
            raise SystemExit(f'{name}: all placed forms must use the connected model without a second rotation.')
        if len(states) != 128:
            raise SystemExit(f'{name}: missing size, form, or facing states.')
        for index, form in enumerate(boulders['forms']):
            for layers in range(1, 9):
                model = models[f'landofdrunir:block/{name}_{form["id"]}_{layers}']
                boxes = [e['from'] + e['to'] for e in model['elements']]
                if boxes != geometry(boulders, form, layers):
                    raise SystemExit(f'{name}: model geometry differs from the boulder catalog.')
                if any(not (0 <= b[i] < b[i+3] <= (layers * 2 if i == 1 else 16)) for b in boxes for i in range(3)):
                    raise SystemExit(f'{name}: boulder exceeds its collision cell.')
            for size in boulders['sizes']:
                identifier = 'landofdrunir:item/' + item_id(material['id'], size['id'], form['id'])
                if identifier not in models:
                    raise SystemExit(f'Missing boulder item model: {identifier}')

    for name in ('golden_birch_tall', 'golden_birch_bent', 'golden_birch_broad'):
        variant = json.loads((worldgen / f'configured_feature/{name}.json').read_text())['config']
        if variant['trunk_provider']['state']['Name'] != 'minecraft:birch_log' or variant['foliage_provider']['state']['Name'] != 'landofdrunir:yellow_birch_leaves':
            raise SystemExit(f'{name}: tree variants must retain birch logs and custom leaves.')
    grove = json.loads((worldgen / 'configured_feature/golden_birch_grove.json').read_text())
    if grove['type'] != 'landofdrunir:golden_grove' or grove['config']:
        raise SystemExit('The grove must use the spaced golden grove feature.')
    if placed['placement'] != [{'type': 'minecraft:biome'}]:
        raise SystemExit('The grove must own spacing without repeated random tree attempts.')
    patch = json.loads((worldgen / 'configured_feature/sun_holly.json').read_text())
    if patch['config']['feature']['feature']['config']['to_place']['state']['Name'] != 'landofdrunir:sun_holly':
        raise SystemExit('Island ground cover must use sun holly.')


    # Rock building parts must use complete vanilla state layouts and the source stone art.
    for block in content['blocks']:
        if block['kind'] not in ('slab', 'stairs'):
            continue
        name, kind = block['name'], block['kind']
        states = json.loads((RESOURCES[1] / f'assets/landofdrunir/blockstates/{name}.json').read_text())['variants']
        if len(states) != (3 if kind == 'slab' else 40):
            raise SystemExit(f'{name}: incomplete vanilla rock part states.')
        model = json.loads((RESOURCES[1] / f'assets/landofdrunir/models/block/{name}.json').read_text())
        if model['parent'] != 'minecraft:block/' + kind:
            raise SystemExit(f'{name}: rock parts must use vanilla geometry.')
        if set(model['textures'].values()) != {'landofdrunir:block/' + block['texture']}:
            raise SystemExit(f'{name}: rock parts must reuse the original stone texture.')

    legacy_forest = json.loads((worldgen / 'biome/golden_birch_island.json').read_text())
    if legacy_forest != biome:
        raise SystemExit('Legacy forest biome must retain the canonical forest definition for saved chunks.')
    for tag in ('is_forest', 'is_overworld'):
        values = json.loads((RESOURCES[1] / f'data/minecraft/tags/worldgen/biome/{tag}.json').read_text())['values']
        if 'landofdrunir:yellow_tree_forest' not in values or 'landofdrunir:golden_birch_island' in values:
            raise SystemExit(f'{tag}: only the canonical forest identifier belongs in biome tags.')


if __name__ == '__main__':
    main()
