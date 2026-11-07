#!/usr/bin/env python3
"""
将 Cayenne DataMap.map.xml 转换为 MySQL DDL
"""

import xml.etree.ElementTree as ET
import re

# Cayenne 类型到 MySQL 类型的映射
TYPE_MAPPING = {
    'INTEGER': 'INT',
    'VARCHAR': 'VARCHAR',
    'TIMESTAMP': 'DATETIME',
    'BIGINT': 'BIGINT',
    'SMALLINT': 'SMALLINT',
    'DECIMAL': 'DECIMAL',
    'FLOAT': 'FLOAT',
    'DOUBLE': 'DOUBLE',
    'BOOLEAN': 'TINYINT(1)',
    'DATE': 'DATE',
    'TIME': 'TIME',
    'BLOB': 'BLOB',
    'CLOB': 'TEXT',
}

def parse_datamap(xml_file):
    """解析 DataMap.map.xml"""
    tree = ET.parse(xml_file)
    root = tree.getroot()

    # 处理命名空间
    ns = {'cayenne': 'http://cayenne.apache.org/schema/3.0/modelMap'}

    tables = []

    # 查找所有 db-entity（表）
    for db_entity in root.findall('.//cayenne:db-entity', ns):
        table_name = db_entity.get('name')
        columns = []
        primary_keys = []

        # 查找所有 db-attribute（列）
        for db_attr in db_entity.findall('cayenne:db-attribute', ns):
            col_name = db_attr.get('name')
            col_type = db_attr.get('type')
            col_length = db_attr.get('length')
            is_pk = db_attr.get('isPrimaryKey') == 'true'
            is_mandatory = db_attr.get('isMandatory') == 'true'

            # 映射类型
            mysql_type = TYPE_MAPPING.get(col_type, col_type)

            # 处理带长度的类型
            if col_length and mysql_type in ['VARCHAR', 'CHAR', 'DECIMAL']:
                mysql_type = f"{mysql_type}({col_length})"

            # 构建列定义
            col_def = f"  `{col_name}` {mysql_type}"
            if is_mandatory or is_pk:
                col_def += " NOT NULL"

            columns.append(col_def)

            if is_pk:
                primary_keys.append(col_name)

        # 构建 CREATE TABLE 语句
        create_stmt = f"CREATE TABLE `{table_name}` (\n"
        create_stmt += ",\n".join(columns)

        if primary_keys:
            pk_list = ", ".join([f"`{pk}`" for pk in primary_keys])
            create_stmt += f",\n  PRIMARY KEY ({pk_list})"

        create_stmt += "\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='';\n"

        tables.append(create_stmt)

    return tables

def main():
    xml_file = r'D:\OPC\AI_Company\01_客户项目\Auto-sale-robot\vms\vms-web\src\main\resources\DataMap.map.xml'
    output_file = r'D:\OPC\AI_Company\01_客户项目\Auto-sale-robot\vms\database\vms_schema.sql'

    print(f"解析 {xml_file}...")
    tables = parse_datamap(xml_file)

    print(f"生成 {len(tables)} 个表的 DDL...")

    # 写入输出文件
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("-- VMS 数据库 Schema (MySQL)\n")
        f.write("-- 从 Cayenne DataMap.map.xml 自动生成\n")
        f.write("-- 生成时间: 2026-11-07\n\n")
        f.write("CREATE DATABASE IF NOT EXISTS vms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n")
        f.write("USE vms;\n\n")

        for table in tables:
            f.write(table)
            f.write("\n")

    print(f"完成！DDL 已写入: {output_file}")

if __name__ == '__main__':
    main()
