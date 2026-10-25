"""Regression checks for malformed content and resource references."""

import contextlib
import io
import json
from pathlib import Path
import shutil
import tempfile
import unittest
from unittest.mock import patch

import validate_resources
import generate_resources


class ResourceValidationTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory(prefix='drunir-resources-')
        self.addCleanup(temporary.cleanup)
        self.resources = []
        for index, source in enumerate(validate_resources.RESOURCES):
            target = Path(temporary.name) / str(index)
            shutil.copytree(source, target)
            self.resources.append(target)
        self.catalog_path = self.resources[0] / 'assets/landofdrunir/content.json'
        self.catalog = json.loads(self.catalog_path.read_text())

    def reject(self, expected):
        with patch.object(validate_resources, 'RESOURCES', self.resources):
            with contextlib.redirect_stdout(io.StringIO()):
                with self.assertRaisesRegex(SystemExit, expected):
                    validate_resources.main()

    def test_unknown_block_kind_is_not_silently_registered_as_stone(self):
        self.catalog['blocks'][0]['kind'] = 'typo'
        self.catalog_path.write_text(json.dumps(self.catalog))
        self.reject('Invalid blocks catalog entry')

    def test_block_item_and_catalog_item_cannot_share_a_registry_identifier(self):
        self.catalog['items'].append({'name': self.catalog['blocks'][0]['name'], 'kind': 'item'})
        self.catalog_path.write_text(json.dumps(self.catalog))
        self.reject('distinct registry identifiers')

    def test_inline_face_must_resolve_to_a_texture(self):
        model = self.resources[0] / 'assets/landofdrunir/models/block/hollow_birch_log.json'
        data = json.loads(model.read_text())
        data['elements'][0]['faces']['up']['texture'] = '#missing'
        model.write_text(json.dumps(data))
        self.reject('unresolved or cyclic texture alias #missing')


class ResourceGenerationTest(unittest.TestCase):
    def test_output_paths_cannot_escape_the_generated_directory(self):
        with tempfile.TemporaryDirectory(prefix='drunir-generation-') as temporary:
            root = Path(temporary)
            output = root / 'generated'
            with patch.object(generate_resources, 'OUTPUT', output):
                for path in ('../escaped.json', str(root / 'absolute.json'),
                             r'..\windows-escape.json', r'C:\escaped.json', 'C:/escaped.json'):
                    with self.assertRaisesRegex(ValueError, 'generated directory'):
                        generate_resources.write(path, {})
            self.assertFalse((root / 'escaped.json').exists())
            self.assertFalse((root / 'absolute.json').exists())

    def test_existing_symlinks_are_rejected_before_generation(self):
        with tempfile.TemporaryDirectory(prefix='drunir-generation-') as temporary:
            root = Path(temporary)
            output = root / 'generated'
            output.mkdir()
            outside = root / 'outside'
            outside.mkdir()
            (output / 'assets').symlink_to(outside, target_is_directory=True)
            with patch.object(generate_resources, 'OUTPUT', output):
                with self.assertRaisesRegex(ValueError, 'symlink'):
                    generate_resources.main()
            self.assertEqual([], list(outside.iterdir()))

    def test_success_removes_stale_outputs_without_changing_current_resources(self):
        with tempfile.TemporaryDirectory(prefix='drunir-generation-') as temporary:
            output = Path(temporary)
            stale = output / 'assets/landofdrunir/models/item/removed.json'
            stale.parent.mkdir(parents=True)
            stale.write_text('{}\n')
            expected = {path.relative_to(generate_resources.OUTPUT): path.read_bytes()
                        for path in generate_resources.OUTPUT.rglob('*.json')}
            with patch.object(generate_resources, 'OUTPUT', output):
                with contextlib.redirect_stdout(io.StringIO()):
                    generate_resources.main()
            actual = {path.relative_to(output): path.read_bytes() for path in output.rglob('*.json')}
            self.assertFalse(stale.exists())
            self.assertEqual(expected, actual)


if __name__ == '__main__':
    unittest.main()
