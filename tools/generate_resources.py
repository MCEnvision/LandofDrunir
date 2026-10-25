import json

from pathlib import Path, PurePosixPath

import boulder_resources

from landscape_resources import boulder_models, rock_models, shore_models, worldgen

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/landofdrunir'
OUTPUT = ROOT / 'src/generated/resources'
CONTENT = json.loads((ASSETS / 'content.json').read_text())
BOULDERS = json.loads((ASSETS / 'boulders.json').read_text())
NAMESPACE = 'landofdrunir'
LANGUAGE = {}
WRITTEN = set()

def write(path, content):
    relative = PurePosixPath(path)
    if '\\' in path or ':' in path or relative.is_absolute() or '..' in relative.parts:
        raise ValueError(f'Resource path must stay inside the generated directory: {path}')
    target = OUTPUT / relative
    if OUTPUT.resolve() != OUTPUT.absolute() or any(part.is_symlink()
            for part in (target, *target.parents) if part.is_relative_to(OUTPUT)):
        raise ValueError(f'Refusing to write through a generated resource symlink: {path}')
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(content, indent=2, ensure_ascii=False) + '\n', encoding='utf-8')
    WRITTEN.add(target)

def asset(path, content):
    write('assets/landofdrunir/' + path + '.json', content)

def texture(name):
    return name if ':' in name else NAMESPACE + ':block/' + name

def title(name):
    return name.replace('_', ' ').title()

def generate_block(entry):
    name, kind = entry['name'], entry['kind']
    model = NAMESPACE + ':block/' + name
    variants = {'': {'model': model}}
    if kind in ('log', 'hollow'):
        if kind == 'log':
            asset('models/block/' + name, {'parent': 'minecraft:block/cube_column', 'textures': {
                'side': texture(entry['side']), 'end': texture(entry['end'])}})
        variants = {'axis=y': {'model': model}, 'axis=z': {'model': model, 'x': 90},
                    'axis=x': {'model': model, 'x': 90, 'y': 90}}
    elif kind == 'door':
        variants = {}
        for half in ('lower', 'upper'):
            for hinge in ('left', 'right'):
                for opened in (False, True):
                    suffix = ('bottom' if half == 'lower' else 'top') + '_' + hinge + ('_open' if opened else '')
                    asset('models/block/' + name + '_' + suffix, {
                        'parent': 'minecraft:block/door_' + suffix,
                        'render_type': 'minecraft:cutout',
                        'textures': {'bottom': texture(entry['bottom']), 'top': texture(entry['top'])}})
                    for facing, rotation in [('east', 0), ('south', 90), ('west', 180), ('north', 270)]:
                        y = (rotation + (90 if opened else 0) + (180 if opened and hinge == 'right' else 0)) % 360
                        key = f'facing={facing},half={half},hinge={hinge},open={str(opened).lower()}'
                        variants[key] = {'model': model + '_' + suffix, 'y': y}
    elif kind == 'trapdoor':
        variants = {}
        for part in ('bottom', 'top', 'open'):
            asset('models/block/' + name + '_' + part, {'parent': 'minecraft:block/template_orientable_trapdoor_' + part,
                  'render_type': 'minecraft:cutout', 'textures': {'texture': texture(entry['texture'])}})
        for half in ('bottom', 'top'):
            for opened in (False, True):
                for facing, y in [('north', 0), ('east', 90), ('south', 180), ('west', 270)]:
                    key = f'facing={facing},half={half},open={str(opened).lower()}'
                    variants[key] = {'model': model + '_' + ('open' if opened else half), 'y': y}
    elif kind == 'boulder':
        variants = boulder_models(name, texture(entry['texture']), CONTENT['boulder_shape'], asset)
    elif kind in ('slab', 'stairs'):
        variants = rock_models(name, kind, texture(entry['texture']), asset)
    elif kind == 'boulder_piece':
        variants = boulder_resources.models(entry, BOULDERS, asset)
    elif kind == 'shore':
        variants = shore_models(name, asset)
    elif kind == 'bottom_top':
        asset('models/block/' + name, {'parent': 'minecraft:block/cube_bottom_top', 'textures': {
            key: texture(entry[key]) for key in ('side', 'top', 'bottom')}})
    elif kind == 'plant':
        asset('models/block/' + name, {'parent': 'minecraft:block/cross', 'render_type': 'minecraft:cutout',
                                      'textures': {'cross': texture(entry['texture'])}})
    else:
        asset('models/block/' + name, {'parent': 'minecraft:block/cube_all',
              **({'render_type': 'minecraft:cutout_mipped'} if kind == 'leaves' else {}),
              'textures': {'all': texture(entry['texture'])}})
    if entry.get('random_y_rotation', False):
        if kind not in ('cube', 'bottom_top', 'leaves', 'plant') or set(variants) != {''}:
            raise ValueError(f'{name}: random rotation requires a non-directional natural block')
        variants = {'': [{'model': model, 'y': angle} for angle in (0, 90, 180, 270)]}
    asset('blockstates/' + name, {'variants': variants})
    item_texture = ASSETS / 'textures/item' / (name + '.png')
    if item_texture.exists():
        item_model = {'parent': 'minecraft:item/generated', 'textures': {'layer0': NAMESPACE + ':item/' + name}}
    elif kind == 'door':
        item_model = {'parent': 'minecraft:item/generated', 'textures': {'layer0': texture(entry['top'])}}
    elif kind == 'plant':
        item_model = {'parent': 'minecraft:item/generated', 'textures': {'layer0': texture(entry['texture'])}}
    else:
        item_model = {'parent': model + ('_bottom' if kind == 'trapdoor' else '')}
    asset('models/item/' + name, item_model)
    LANGUAGE['block.landofdrunir.' + name] = title(name)
    conditions = [{'condition': 'minecraft:survives_explosion'}]
    if kind == 'door':
        conditions.append({'condition': 'minecraft:block_state_property', 'block': NAMESPACE + ':' + name,
                           'properties': {'half': 'lower'}})
    functions = []
    if kind in ('boulder', 'slab'):
        functions.append({'function': 'minecraft:set_count', 'count': 2, 'conditions': [{
            'condition': 'minecraft:block_state_property', 'block': NAMESPACE + ':' + name,
            'properties': {'type': 'double'}}]})
    write('data/landofdrunir/loot_table/blocks/' + name + '.json', {'type': 'minecraft:block', 'pools': [{
        'rolls': 1, 'conditions': conditions,
        'entries': [{'type': 'minecraft:item', 'name': NAMESPACE + ':' + name,
                     **({'functions': functions} if functions else {})}]}]})

def main():
    if OUTPUT.resolve() != OUTPUT.absolute() or OUTPUT.is_symlink() or any(path.is_symlink() for path in OUTPUT.rglob('*')):
        raise ValueError('The generated directory must not contain symlinks.')
    LANGUAGE.clear()
    WRITTEN.clear()
    for block in CONTENT['blocks']:
        generate_block(block)

    for entry in CONTENT['items']:
        parent = 'minecraft:item/handheld' if entry['kind'] in ('sword', 'lance', 'axe', 'pickaxe', 'shovel', 'hoe') else 'minecraft:item/generated'
        asset('models/item/' + entry['name'], {'parent': parent, 'textures': {'layer0': NAMESPACE + ':item/' + entry['texture']}})
        LANGUAGE['item.landofdrunir.' + entry['name']] = title(entry['name'])

    for name in CONTENT['creatures']:
        asset('models/item/' + name + '_spawn_egg', {'parent': 'minecraft:item/template_spawn_egg'})
        LANGUAGE['item.landofdrunir.' + name + '_spawn_egg'] = title(name) + ' Spawn Egg'
        LANGUAGE['entity.landofdrunir.' + name] = title(name)
    LANGUAGE['entity.landofdrunir.magmafiend_fireball'] = 'Magmafiend Fireball'

    for name in CONTENT['textures']:
        layer = ('minecraft:trims/items/helmet_trim_drunir_' + name.rsplit('/', 1)[1]
                 if name.startswith('trims/color_palettes/') else NAMESPACE + ':' + name)
        asset('models/item/texture/' + name, {'parent': 'minecraft:item/generated', 'textures': {'layer0': layer}})
        LANGUAGE['item.landofdrunir.texture.' + name.replace('/', '.')] = (
            title(name.rsplit('/', 1)[1]) + ' Trim Palette Preview'
            if name.startswith('trims/color_palettes/') else 'Texture: ' + name)

    write('assets/minecraft/atlases/blocks.json', {'sources': [
        {'type': 'minecraft:single', 'resource': NAMESPACE + ':' + name}
        for name in CONTENT['textures'] if not name.startswith(('block/', 'item/', 'trims/'))
    ] + [{
        'type': 'minecraft:paletted_permutations',
        'textures': ['minecraft:trims/items/helmet_trim'],
        'palette_key': 'minecraft:trims/color_palettes/trim_palette',
        'permutations': {'drunir_' + name.rsplit('/', 1)[1]: NAMESPACE + ':' + name
                         for name in CONTENT['textures'] if name.startswith('trims/color_palettes/')}
    }]})

    for name, label in [('blocks', 'Drunir Blocks'), ('items', 'Drunir Items'), ('creatures', 'Drunir Creatures'), ('textures', 'Drunir Texture Gallery')]:
        LANGUAGE['tab.landofdrunir.' + name] = label
    LANGUAGE['biome.landofdrunir.golden_birch_shore'] = 'Golden Birch Shore'
    LANGUAGE['biome.landofdrunir.yellow_tree_forest'] = 'Yellow Tree Forest'
    LANGUAGE['biome.landofdrunir.golden_birch_island'] = 'Yellow Tree Forest'
    LANGUAGE['biome.landofdrunir.golden_birch_waters'] = 'Golden Birch Waters'
    LANGUAGE['command.landofdrunir.island.location'] = 'Yellow Tree Forest: X %s, Z %s in the Overworld. Only new chunks receive the island terrain.'
    LANGUAGE['command.landofdrunir.island.unavailable'] = 'Yellow Tree Forest requires a standard Overworld terrain generator.'
    worldgen(ROOT, write)
    boulder_resources.generate(BOULDERS, asset, write, LANGUAGE)
    asset('lang/en_us', LANGUAGE)
    write('data/minecraft/tags/block/dirt.json', {'replace': False, 'values': ['landofdrunir:yellow_grass_block', 'landofdrunir:golden_shore_sand']})
    write('data/minecraft/tags/worldgen/biome/is_overworld.json', {'replace': False, 'values': ['landofdrunir:yellow_tree_forest', 'landofdrunir:golden_birch_shore', 'landofdrunir:golden_birch_waters']})
    write('data/minecraft/tags/worldgen/biome/is_forest.json', {'replace': False, 'values': ['landofdrunir:yellow_tree_forest']})
    write('data/minecraft/tags/block/mineable/pickaxe.json', {'replace': False, 'values': [NAMESPACE + ':' + b['name'] for b in CONTENT['blocks'] if b['kind'] in ('cube', 'bottom_top', 'boulder', 'boulder_piece', 'slab', 'stairs') and 'planks' not in b['name']]})
    write('data/minecraft/tags/block/mineable/axe.json', {'replace': False, 'values': [NAMESPACE + ':' + b['name'] for b in CONTENT['blocks'] if b['kind'] in ('log', 'hollow', 'door', 'trapdoor') or 'planks' in b['name']]})
    write('data/minecraft/tags/block/logs.json', {'replace': False, 'values': [NAMESPACE + ':' + b['name'] for b in CONTENT['blocks'] if b['kind'] in ('log', 'hollow') and 'planks' not in b['name'] and 'pillar' not in b['name']]})
    write('data/minecraft/tags/block/leaves.json', {'replace': False, 'values': [NAMESPACE + ':' + b['name'] for b in CONTENT['blocks'] if b['kind'] == 'leaves']})

    write('data/minecraft/tags/block/mineable/shovel.json', {'replace': False, 'values': ['landofdrunir:golden_shore_sand']})
    # This directory is owned by this generator; remove obsolete outputs only after success.
    for path in OUTPUT.rglob('*.json'):
        if path not in WRITTEN:
            if path.is_symlink():
                raise ValueError(f'Refusing to remove a generated resource symlink: {path}')
            path.unlink()
    print(f"Generated resources for {len(CONTENT['blocks'])} blocks, {len(CONTENT['items'])} items, {len(CONTENT['creatures'])} creatures, and {len(CONTENT['textures'])} texture samples.")


if __name__ == '__main__':
    main()
