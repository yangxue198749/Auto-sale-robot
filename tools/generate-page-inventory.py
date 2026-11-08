#!/usr/bin/env python3
"""
Scan VMS controllers and menu to generate page inventory and permission matrix.
"""

import re
import json
from pathlib import Path
from collections import defaultdict

# Parse menu-vms.md
def parse_menu(menu_file):
    """Parse menu structure from markdown table."""
    menu_items = []
    with open(menu_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    for line in lines[2:]:  # Skip header and separator
        line = line.strip()
        if not line or line.startswith('---'):
            continue

        parts = [p.strip() for p in line.split('|')]
        if len(parts) >= 7:
            menu_items.append({
                'id': parts[0],
                'name': parts[1],
                'path': parts[2],
                'code': parts[3],
                'type': parts[4],
                'parent_id': parts[5],
                'sort': parts[6]
            })

    return menu_items

# Scan controllers for @RequestMapping
def scan_controllers(controllers_dir):
    """Scan all controller files for request mappings."""
    endpoints = []

    for controller_file in Path(controllers_dir).glob('*Controller.java'):
        with open(controller_file, 'r', encoding='utf-8') as f:
            content = f.read()

        # Extract class-level @RequestMapping
        class_mapping = None
        if match := re.search(r'@RequestMapping\s*\(\s*["\']([^"\']+)["\']', content):
            class_mapping = match.group(1)

        # Extract method-level @RequestMapping
        method_pattern = r'@RequestMapping\s*\(\s*(?:value\s*=\s*)?["\']([^"\']+)["\'][^)]*\)\s*(?:@\w+(?:\([^)]*\))?\s*)*(?:public|private|protected)\s+\w+\s+(\w+)\s*\('

        for match in re.finditer(method_pattern, content, re.MULTILINE | re.DOTALL):
            path = match.group(1)
            method_name = match.group(2)

            # Build full path
            if class_mapping:
                full_path = f"/{class_mapping}/{path}".replace('//', '/')
            else:
                full_path = f"/{path}"

            endpoints.append({
                'controller': controller_file.stem,
                'method': method_name,
                'path': full_path,
                'file': str(controller_file)
            })

    return endpoints

# Map menu items to endpoints
def map_menu_to_endpoints(menu_items, endpoints):
    """Map menu items to their corresponding endpoints."""
    mapping = []

    for menu in menu_items:
        if menu['type'] not in ['menu', 'button']:
            continue

        menu_path = menu['path']
        if not menu_path or menu_path == '#':
            continue

        # Find matching endpoints
        matching = [e for e in endpoints if menu_path in e['path'] or e['path'] in menu_path]

        mapping.append({
            'menu_id': menu['id'],
            'menu_name': menu['name'],
            'menu_path': menu_path,
            'menu_code': menu['code'],
            'menu_type': menu['type'],
            'parent_id': menu['parent_id'],
            'endpoints': matching,
            'migration_status': 'pending'  # pending, migrated, skipped
        })

    return mapping

# Main
def main():
    base_dir = Path(__file__).parent.parent

    # Parse menu
    menu_file = base_dir / 'menu-vms.md'
    menu_items = parse_menu(menu_file)
    print(f"Parsed {len(menu_items)} menu items")

    # Scan controllers
    controllers_dir = base_dir / 'vms-web' / 'src' / 'main' / 'java' / 'com' / 'kdx' / 'vms' / 'web' / 'controller'
    endpoints = scan_controllers(controllers_dir)
    print(f"Scanned {len(endpoints)} endpoints from {len(set(e['controller'] for e in endpoints))} controllers")

    # Map menu to endpoints
    mapping = map_menu_to_endpoints(menu_items, endpoints)

    # Save page inventory
    inventory_file = base_dir / 'docs' / 'migration' / 'page-inventory.json'
    inventory_file.parent.mkdir(parents=True, exist_ok=True)

    with open(inventory_file, 'w', encoding='utf-8') as f:
        json.dump({
            'total_menu_items': len(menu_items),
            'total_endpoints': len(endpoints),
            'mapped_items': len(mapping),
            'items': mapping
        }, f, indent=2, ensure_ascii=False)

    print(f"Page inventory saved to {inventory_file}")

    # Generate permission matrix (simplified)
    permission_matrix = []
    for item in mapping:
        if item['menu_code']:
            permission_matrix.append({
                'menu_id': item['menu_id'],
                'menu_name': item['menu_name'],
                'permission_code': item['menu_code'],
                'path': item['menu_path'],
                'type': item['menu_type'],
                'requires_auth': True,  # All JSP pages require auth
                'requires_permission': bool(item['menu_code']),
                'migration_priority': 'high' if item['menu_type'] == 'menu' else 'medium'
            })

    permission_file = base_dir / 'docs' / 'migration' / 'permission-matrix.json'
    with open(permission_file, 'w', encoding='utf-8') as f:
        json.dump({
            'total_permissions': len(permission_matrix),
            'permissions': permission_matrix
        }, f, indent=2, ensure_ascii=False)

    print(f"Permission matrix saved to {permission_file}")

if __name__ == '__main__':
    main()
