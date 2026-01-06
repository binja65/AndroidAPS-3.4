#!/usr/bin/env python3
"""
Comprehensive duplicate string resource analyzer for AndroidAPS3
Finds:
1. Same string name in multiple modules
2. Same English value with different names
3. Preference-related duplicates
"""

import xml.etree.ElementTree as ET
from pathlib import Path
from collections import defaultdict
import re

def parse_strings_xml(file_path):
    """Parse a strings.xml file and return dict of name->value"""
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
        strings = {}
        for string_elem in root.findall('string'):
            name = string_elem.get('name')
            # Get text content, handling CDATA and HTML
            value = ''.join(string_elem.itertext()).strip()
            if name and value:
                strings[name] = value
        return strings
    except Exception as e:
        print(f"Error parsing {file_path}: {e}")
        return {}

def find_all_strings_files(base_path):
    """Find all strings.xml files in the project"""
    base = Path(base_path)
    return list(base.glob('**/res/values/strings.xml'))

def analyze_duplicates(base_path):
    """Main analysis function"""

    # Find all strings.xml files
    strings_files = find_all_strings_files(base_path)
    print(f"Found {len(strings_files)} strings.xml files\n")

    # Track all strings by name and by value
    strings_by_name = defaultdict(list)  # name -> [(module, value)]
    strings_by_value = defaultdict(list)  # value -> [(module, name)]
    all_strings = {}  # file_path -> {name: value}

    # Parse all files
    for file_path in strings_files:
        # Get module name from path
        parts = str(file_path).split('\\')
        if 'src' in parts:
            src_idx = parts.index('src')
            module = '\\'.join(parts[:src_idx])
        else:
            module = str(file_path.parent.parent.parent.parent)

        strings = parse_strings_xml(file_path)
        all_strings[str(file_path)] = strings

        for name, value in strings.items():
            strings_by_name[name].append((module, value, str(file_path)))
            strings_by_value[value].append((module, name, str(file_path)))

    # Find duplicates by name (same name in multiple modules)
    name_duplicates = {
        name: locations
        for name, locations in strings_by_name.items()
        if len(locations) > 1
    }

    # Find duplicates by value (same value, different names)
    value_duplicates = {
        value: locations
        for value, locations in strings_by_value.items()
        if len(locations) > 1 and len(value) > 5  # Ignore very short strings
    }

    # Priority modules for preference analysis
    priority_modules = [
        'plugins\\main',
        'core\\keys',
        'core\\ui',
        'plugins\\configuration',
        'app'
    ]

    # Preference-related keywords
    pref_keywords = [
        'pref_title', 'pref_summary', '_title', '_summary',
        'enable_', 'show_', 'use_', 'allow_'
    ]

    return {
        'name_duplicates': name_duplicates,
        'value_duplicates': value_duplicates,
        'all_strings': all_strings,
        'strings_files': strings_files,
        'priority_modules': priority_modules,
        'pref_keywords': pref_keywords
    }

def print_report(results):
    """Print comprehensive duplicate report"""

    print("="*80)
    print("ANDROIDAPS3 DUPLICATE STRING RESOURCES ANALYSIS")
    print("="*80)
    print()

    # HIGH PRIORITY: Exact duplicates (same name, same value in multiple files)
    print("="*80)
    print("HIGH PRIORITY: EXACT DUPLICATES (Same name, same value in multiple files)")
    print("="*80)
    print()

    exact_duplicates = []
    for name, locations in results['name_duplicates'].items():
        # Check if all values are the same
        values = [loc[1] for loc in locations]
        if len(set(values)) == 1:
            exact_duplicates.append((name, locations))

    exact_duplicates.sort(key=lambda x: len(x[1]), reverse=True)

    for name, locations in exact_duplicates:
        print(f"String: {name}")
        print(f"Value: {locations[0][1]}")
        print(f"Found in {len(locations)} locations:")
        for module, value, filepath in locations:
            print(f"  - {filepath}")
        print(f"Recommendation: Keep in ONE shared module, remove from others")
        print()

    print(f"Total exact duplicates: {len(exact_duplicates)}")
    print()

    # HIGH PRIORITY: Same name, DIFFERENT values (conflicts!)
    print("="*80)
    print("HIGH PRIORITY: CONFLICTS (Same name, DIFFERENT values)")
    print("="*80)
    print()

    conflicts = []
    for name, locations in results['name_duplicates'].items():
        values = set([loc[1] for loc in locations])
        if len(values) > 1:
            conflicts.append((name, locations))

    conflicts.sort(key=lambda x: x[0])

    for name, locations in conflicts:
        print(f"String: {name}")
        print(f"Found in {len(locations)} locations with DIFFERENT values:")
        for module, value, filepath in locations:
            print(f"  - {filepath}")
            print(f"    Value: {value[:100]}")
        print(f"Recommendation: Rename to make unique OR consolidate to single definition")
        print()

    print(f"Total conflicts: {len(conflicts)}")
    print()

    # MEDIUM PRIORITY: Same value, different names
    print("="*80)
    print("MEDIUM PRIORITY: SAME VALUE, DIFFERENT NAMES (Potential consolidation)")
    print("="*80)
    print()

    value_dups = []
    for value, locations in results['value_duplicates'].items():
        # Filter out very common values
        if value.lower() not in ['ok', 'cancel', 'yes', 'no', 'save', 'delete', 'close']:
            value_dups.append((value, locations))

    # Sort by number of occurrences
    value_dups.sort(key=lambda x: len(x[1]), reverse=True)

    # Show top 50
    for value, locations in value_dups[:50]:
        if len(locations) <= 10:  # Don't show if too many
            print(f"Value: {value[:80]}")
            print(f"Found with {len(locations)} different names:")
            for module, name, filepath in locations:
                print(f"  - {name} in {filepath}")
            print(f"Recommendation: Consider using single string resource")
            print()

    print(f"Total value duplicates: {len(value_dups)} (showing top 50 with <=10 instances)")
    print()

    # PREFERENCE STRINGS ANALYSIS
    print("="*80)
    print("PREFERENCE-RELATED STRING ANALYSIS")
    print("="*80)
    print()

    pref_strings = defaultdict(list)
    for filepath, strings in results['all_strings'].items():
        for name, value in strings.items():
            # Check if preference-related
            is_pref = any(keyword in name.lower() for keyword in results['pref_keywords'])
            if is_pref:
                pref_strings[name].append((filepath, value))

    # Find preference duplicates
    pref_duplicates = {
        name: locations
        for name, locations in pref_strings.items()
        if len(locations) > 1
    }

    print(f"Found {len(pref_duplicates)} preference strings in multiple locations:")
    print()

    for name, locations in sorted(pref_duplicates.items()):
        values = set([loc[1] for loc in locations])
        if len(values) == 1:
            print(f"Pref: {name}")
            print(f"Value: {locations[0][1][:80]}")
            print(f"Locations:")
            for filepath, value in locations:
                print(f"  - {filepath}")
            print()

    # SUMMARY STATISTICS
    print("="*80)
    print("SUMMARY STATISTICS")
    print("="*80)
    print()
    print(f"Total strings.xml files: {len(results['strings_files'])}")
    print(f"Total unique string names: {len(results['name_duplicates']) + sum(1 for v in results['all_strings'].values() for _ in v)}")
    print(f"Exact duplicates (same name, same value): {len(exact_duplicates)}")
    print(f"Conflicts (same name, different value): {len(conflicts)}")
    print(f"Value duplicates (same value, different names): {len(value_dups)}")
    print(f"Preference duplicates: {len(pref_duplicates)}")
    print()

if __name__ == '__main__':
    base_path = r'E:\GitHub\AndroidAPS3'
    results = analyze_duplicates(base_path)
    print_report(results)
