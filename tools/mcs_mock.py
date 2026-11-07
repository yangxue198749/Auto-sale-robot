#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MCS Mock 服务（VMS 本地开发用）

背景：真实 MCS 是售货机消息中枢，VMS 通过 HTTP POST 向其下发设备指令
（货道库存、商品、价格、开门密码等）。本地无真实 MCS，用本服务模拟：
  - 对所有 POST 返回 {"success": 0}（MessageResult 约定 0=成功）
  - 记录每个请求的路径和消息类型到日志文件，供测试断言
  - 零第三方依赖，仅用 Python 标准库

启动：python mcs_mock.py [port]   默认端口 8011
日志：同目录 mcs_mock.log（记录每次请求的 msgType / 路径 / 时间）
"""
import json
import sys
import datetime
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 8011
LOG_FILE = "mcs_mock.log"


class McsHandler(BaseHTTPRequestHandler):
    def _record(self, body: bytes):
        try:
            payload = json.loads(body.decode("utf-8"))
            # VMS 消息结构通常为 {"head": {...}, "data": {"msgType": ...}}
            data = payload.get("data", {}) if isinstance(payload, dict) else {}
            msg_type = data.get("msgType", "unknown")
        except Exception:
            msg_type = "non-json"
        line = "%s %s msgType=%s\n" % (
            datetime.datetime.now().isoformat(timespec="seconds"),
            self.path, msg_type)
        with open(LOG_FILE, "a", encoding="utf-8") as f:
            f.write(line)
        # 同时打印到控制台
        sys.stdout.write(line)
        sys.stdout.flush()

    def do_POST(self):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length) if length else b""
        self._record(body)
        # MessageResult：success=0 代表成功
        resp = json.dumps({"success": 0, "sn": "mock", "from": "mcs-mock"}).encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "application/json;charset=UTF-8")
        self.send_header("Content-Length", str(len(resp)))
        self.end_headers()
        self.wfile.write(resp)

    def do_GET(self):
        # 健康检查
        resp = b'{"status":"ok","service":"mcs-mock"}'
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(resp)))
        self.end_headers()
        self.wfile.write(resp)

    def log_message(self, fmt, *args):
        # 静音默认访问日志（已自定义记录）
        pass


if __name__ == "__main__":
    server = ThreadingHTTPServer(("0.0.0.0", PORT), McsHandler)
    print("MCS mock listening on 0.0.0.0:%d (success=0 for all POST)" % PORT)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        server.shutdown()
