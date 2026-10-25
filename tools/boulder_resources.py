"""Build modular boulder models, items, drops, and natural placement states."""

FACES = ('down', 'up', 'north', 'south', 'west', 'east')


def block_id(material):
    return material + '_boulder_piece'


def item_id(material, size, form):
    return material + '_' + size + '_' + form + '_boulder'


def geometry(catalog, form, layers):
    width = catalog['widths'][layers - 1] / 16
    height = layers / 8
    return [[8 + (box[0] - 8) * width, box[1] * height, 8 + (box[2] - 8) * width,
             8 + (box[3] - 8) * width, box[4] * height, 8 + (box[5] - 8) * width]
            for box in form['boxes']]


def stone_texture(material):
    return 'landofdrunir:block/' + material + ('_top' if 'sandstone' in material else '')


def models(entry, catalog, asset):
    name = entry['name']
    variants = {}
    for index, form in enumerate(catalog['forms']):
        for layers in range(1, 9):
            model = f'{name}_{form["id"]}_{layers}'
            texture = stone_texture(name.removesuffix('_boulder_piece'))
            asset('models/block/' + model, {
                'parent': 'minecraft:block/block',
                'textures': {'stone': texture, 'particle': texture},
                'elements': [{'from': box[:3], 'to': box[3:],
                              'faces': {face: {'texture': '#stone'} for face in FACES}}
                             for box in geometry(catalog, form, layers)]})
            for facing in ('north', 'east', 'south', 'west'):
                variants[f'facing={facing},form={index},layers={layers}'] = {
                    'model': 'landofdrunir:block/' + name}
    asset('models/block/' + name, {
        'parent': 'minecraft:block/block', 'loader': 'landofdrunir:connected_boulder',
        'fallback': f'landofdrunir:block/{name}_rounded_4',
        'textures': {'stone': texture, 'particle': texture}})
    return variants


def generate(catalog, asset, write, language):
    entries = []
    for material in catalog['materials']:
        block = block_id(material['id'])
        language['item.landofdrunir.' + block] = 'Medium ' + material['id'].replace('_', ' ').title() + ' Boulder (Rounded)'
        pools = []
        for index, form in enumerate(catalog['forms']):
            pools.append({'rolls': 1, 'conditions': [
                {'condition': 'minecraft:survives_explosion'},
                {'condition': 'minecraft:block_state_property', 'block': 'landofdrunir:' + block,
                 'properties': {'form': str(index)}}], 'entries': [{
                    'type': 'minecraft:item',
                    'name': 'landofdrunir:' + item_id(material['id'], 'tiny', form['id']),
                    'functions': [{'function': 'minecraft:set_count', 'count': layers, 'conditions': [{
                        'condition': 'minecraft:block_state_property', 'block': 'landofdrunir:' + block,
                        'properties': {'layers': str(layers)}}]} for layers in range(1, 9)]}]})
        write('data/landofdrunir/loot_table/blocks/' + block + '.json', {'type': 'minecraft:block', 'pools': pools})
        for size in catalog['sizes']:
            for index, form in enumerate(catalog['forms']):
                name = item_id(material['id'], size['id'], form['id'])
                asset('models/item/' + name, {
                    'parent': f'landofdrunir:block/{block}_{form["id"]}_{size["layers"]}'})
                language['item.landofdrunir.' + name] = (
                    size['id'].title() + ' ' + material['id'].replace('_', ' ').title()
                    + ' Boulder (' + form['id'].title() + ')')
                for facing in ('north', 'east', 'south', 'west'):
                    entries.append({'data': {'Name': 'landofdrunir:' + block, 'Properties': {
                        'form': str(index), 'layers': str(size['layers']), 'facing': facing,
                        'waterlogged': 'false'}}, 'weight': material['weight'] * size['weight']})
    write('data/landofdrunir/worldgen/configured_feature/island_boulders.json', {
        'type': 'minecraft:simple_block', 'config': {
            'to_place': {'type': 'minecraft:weighted_state_provider', 'entries': entries}}})
    language['tab.landofdrunir.boulders'] = 'Drunir Boulders'
    language['tooltip.landofdrunir.boulder.build'] = 'Add a matching stone piece to build this boulder up.'
    language['tooltip.landofdrunir.boulder.separate'] = 'Sneak to place a separate piece.'
