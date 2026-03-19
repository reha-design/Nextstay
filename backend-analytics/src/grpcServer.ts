import * as grpc from '@grpc/grpc-js';
import * as protoLoader from '@grpc/proto-loader';
import path from 'path';
import { Database } from "bun:sqlite";

// 🗄️ SQLite 데이터베이스 인스턴스 (공유를 위해 외부에서 주입받는 것이 좋으나, 여기선 예시로 직접 로드)
const db = new Database("analytics.sqlite", { create: true });

const PROTO_PATH = path.resolve(__dirname, '../../proto/analytics.proto');

const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true,
});

const grpcObj = grpc.loadPackageDefinition(packageDefinition) as any;
const analyticsProto = grpcObj.com.mrmention.nextstay.grpc;

/**
 * gRPC 서비스 구현체
 */
const serviceHandlers = {
  // 1. 이벤트 로그 처리
  logEvent: (call: any, callback: any) => {
    const { event_name, payload_json, timestamp } = call.request;
    
    console.log(`[gRPC-EVENT] 📊 ${event_name} | ${new Date().toLocaleTimeString()}`);
    
    db.run(
      "INSERT INTO event_logs (event_name, payload, timestamp) VALUES (?, ?, ?)",
      [event_name, payload_json, timestamp]
    );

    callback(null, { success: true, message: "Event logged via gRPC" });
  },

  // 2. 방문 로그 처리
  logVisit: (call: any, callback: any) => {
    const { path, user_id, user_agent } = call.request;
    
    console.log(`[gRPC-VISIT] 🏃 ${path} | User: ${user_id || 'Guest'} | ${new Date().toLocaleTimeString()}`);

    db.run(
      "INSERT INTO visit_logs (path, user_id, user_agent) VALUES (?, ?, ?)",
      [path, user_id || null, user_agent]
    );

    callback(null, { success: true, message: "Visit logged via gRPC" });
  }
};

/**
 * gRPC 서버 시작 함수
 */
export function startGrpcServer(port: number = 50051) {
  const server = new grpc.Server();
  server.addService(analyticsProto.AnalyticsService.service, serviceHandlers);
  
  server.bindAsync(`0.0.0.0:${port}`, grpc.ServerCredentials.createInsecure(), (err, actualPort) => {
    if (err) {
      console.error(`❌ gRPC Server failed to bind: ${err.message}`);
      return;
    }
    console.log(`🚀 gRPC Analytics Server is running at 0.0.0.0:${actualPort}`);
  });
}
