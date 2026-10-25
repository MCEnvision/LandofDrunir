"""Models and worldgen resources for the island landscape."""

from copy import deepcopy
import json

FACES = ('down', 'up', 'north', 'south', 'west', 'east')
def boulder_models(name, texture, boxes, asset):
    elements = [{'from': low, 'to': high,
                 'faces': {face: {'texture': '#stone'} for face in FACES}}
                for box in boxes for low, high in [(box[:3], box[3:])]]
    upper = deepcopy(elements)
    for element in upper:
        element['from'][1] += 8
        element['to'][1] += 8
    for suffix, geometry in [('', elements), ('_top', upper), ('_double', elements + upper)]:
        asset('models/block/' + name + suffix, {
            'parent': 'minecraft:block/block', 'textures': {'stone': texture, 'particle': texture},
            'elements': geometry})
    return {f'facing={facing},type={kind}': {
                'model': 'landofdrunir:block/' + name + suffix, 'y': rotation}
            for facing, rotation in [('north', 0), ('east', 90), ('south', 180), ('west', 270)]
            for kind, suffix in [('bottom', ''), ('top', '_top'), ('double', '_double')]}


def rock_models(name, kind, texture, asset):
    model = 'landofdrunir:block/' + name
    textures = {face: texture for face in ('bottom', 'top', 'side')}
    if kind == 'slab':
        for suffix, parent in [('', 'slab'), ('_top', 'slab_top')]:
            asset('models/block/' + name + suffix,
                  {'parent': 'minecraft:block/' + parent, 'textures': textures})
        asset('models/block/' + name + '_double',
              {'parent': 'minecraft:block/cube_all', 'textures': {'all': texture}})
        return {'type=' + part: {'model': model + suffix}
                for part, suffix in [('bottom', ''), ('top', '_top'), ('double', '_double')]}
    for suffix, parent in [('', 'stairs'), ('_inner', 'inner_stairs'), ('_outer', 'outer_stairs')]:
        asset('models/block/' + name + suffix,
              {'parent': 'minecraft:block/' + parent, 'textures': textures})
    variants = {}
    for facing, angle in [('east', 0), ('south', 90), ('west', 180), ('north', 270)]:
        for half in ('bottom', 'top'):
            for shape in ('straight', 'inner_left', 'inner_right', 'outer_left', 'outer_right'):
                suffix = '' if shape == 'straight' else '_' + shape.split('_')[0]
                rotation = angle
                if shape.endswith('left') and half == 'bottom':
                    rotation -= 90
                if shape.endswith('right') and half == 'top':
                    rotation += 90
                state = {'model': model + suffix}
                if rotation % 360:
                    state['y'] = rotation % 360
                if half == 'top':
                    state['x'] = 180
                if half == 'top' or rotation % 360:
                    state['uvlock'] = True
                variants[f'facing={facing},half={half},shape={shape}'] = state
    return variants


def shore_models(name, asset):
    # Spread the grass fragments evenly before increasing their coverage.
    threshold = ((0, 8, 2, 10), (12, 4, 14, 6), (3, 11, 1, 9), (15, 7, 13, 5))
    coverage = (0, 0, 1, 3, 6, 10, 14, 16)
    for tone, count in enumerate(coverage):
        elements = [{'from': [0, 0, 0], 'to': [16, 16, 16],
                     'faces': {face: {'texture': '#sand', 'tintindex': 0, 'cullface': face}
                               for face in FACES}}]
        for row in range(4):
            for column in range(4):
                if threshold[row][column] < count:
                    x, z = column * 4, row * 4
                    elements.append({'from': [x, 16.002, z], 'to': [x + 4, 16.002, z + 4],
                                     'shade': False, 'faces': {'up': {
                                         'texture': '#grass', 'uv': [x, z, x + 4, z + 4],
                                         'cullface': 'up'}}})
        asset(f'models/block/{name}_{tone}', {
            'parent': 'minecraft:block/block',
            'textures': {'sand': 'minecraft:block/sand',
                         'grass': 'landofdrunir:block/yellow_grass_block_top',
                         'particle': 'minecraft:block/sand'}, 'elements': elements})
    asset('models/block/' + name, {'parent': f'landofdrunir:block/{name}_0'})
    return {f'tone={tone}': [{'model': f'landofdrunir:block/{name}_{tone}', 'y': angle}
                           for angle in (0, 90, 180, 270)] for tone in range(8)}


def worldgen(root, write):
    templates = root / 'tools/worldgen'
    resources = (
        ('biome', 'yellow_tree_forest', 'yellow_tree_forest'),
        ('biome', 'golden_birch_island', 'yellow_tree_forest'),
        ('biome', 'golden_birch_shore', 'golden_birch_shore'),
        ('biome', 'golden_birch_waters', 'golden_birch_waters'),
        *(("configured_feature", name, name) for name in ("golden_birch_tall", "golden_birch_bent", "golden_birch_broad", "golden_birch_grove", "sun_holly", "sunken_ship")),
        ('placed_feature', 'sun_holly_patch', 'sun_holly_placed'),
        ('placed_feature', 'sunken_ships', 'sunken_ships'),
        ('configured_feature', 'golden_birch', 'golden_birch'),
        ('placed_feature', 'golden_birch_trees', 'golden_birch_trees'),
        ('configured_feature', 'shore_blend', 'shore_blend'),
        ('placed_feature', 'shore_blend', 'shore_blend_placed'),
        ('placed_feature', 'island_boulders', 'island_boulders_placed'),
        ('configured_feature', 'island_outcrop', 'island_outcrop'),
        ('placed_feature', 'island_outcrops', 'island_outcrops'),
    )
    for registry, name, source in resources:
        write(f'data/landofdrunir/worldgen/{registry}/{name}.json',
              json.loads((templates / (source + '.json')).read_text()))
