import { Elysia, t } from "elysia";
import { Database } from "bun:sqlite";
import { cors } from "@elysiajs/cors";
import { rateLimit } from "elysia-rate-limit";

// 🗄️ SQLite 데이터베이스 초기화 및 WAL 모드 설정
const db = new Database("analytics.sqlite", { create: true });
db.exec("PRAGMA journal_mode = WAL;");
db.exec("PRAGMA synchronous = NORMAL;");

// 테이블 초기화
db.run(`
  CREATE TABLE IF NOT EXISTS visit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    path TEXT NOT NULL,
    user_id TEXT,
    user_agent TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )
`);

db.run(`
  CREATE TABLE IF NOT EXISTS event_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_name TEXT NOT NULL,
    payload TEXT,
    timestamp INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )
`);

const app = new Elysia()
  .use(cors())
  .use(rateLimit({
    duration: 60000,
    max: 30,
    errorResponse: "Too many requests, please try again later."
  }))
  .get("/", () => ({ status: "ok", engine: "Bun + Elysia + SQLite (WAL)" }))
  
  // 1. 단순 이벤트 로깅
  .post("/analytics/event", ({ body }) => {
    const { eventName, payload, timestamp } = body;
    
    // 📢 서버 로그 출력
    console.log(`[EVENT] 📊 ${eventName} | ${new Date().toLocaleTimeString()}`);
    console.log(` > Payload: ${JSON.stringify(payload)}`);
    
    db.run(
      "INSERT INTO event_logs (event_name, payload, timestamp) VALUES (?, ?, ?)",
      [eventName, JSON.stringify(payload), timestamp]
    );

    return { success: true };
  }, {
    body: t.Object({
      eventName: t.String(),
      payload: t.Unknown(),
      timestamp: t.Number(),
    })
  })
  
  // 2. 방문자 로깅 전용 API
  .post("/stats/v1/visits", ({ body }) => {
    const { path, userId, userAgent } = body;
    
    // 📢 방문 로그 출력
    console.log(`[VISIT] 🏃 ${path} | User: ${userId || 'Guest'} | ${new Date().toLocaleTimeString()}`);
    console.log(` > UA: ${userAgent.substring(0, 60)}${userAgent.length > 60 ? '...' : ''}`);

    db.run(
      "INSERT INTO visit_logs (path, user_id, user_agent) VALUES (?, ?, ?)",
      [path, userId || null, userAgent]
    );
    
    return { 
      success: true, 
      message: "Visit logged to SQLite (WAL)"
    };
  }, {
    body: t.Object({
      path: t.String(),
      userId: t.Optional(t.String()),
      userAgent: t.String()
    })
  })
  
  // 3. 오늘 방문자 통계 조회 (실제 DB 데이터 기반)
  .get("/stats/v1/visits/today", () => {
    const stats = db.query(`
      SELECT 
        COUNT(*) as totalVisits,
        COUNT(DISTINCT user_id) as uniqueVisitors
      FROM visit_logs 
      WHERE date(created_at) = date('now')
    `).get() as { totalVisits: number, uniqueVisitors: number };

    const topPaths = db.query(`
      SELECT path, COUNT(*) as count 
      FROM visit_logs 
      WHERE date(created_at) = date('now')
      GROUP BY path 
      ORDER BY count DESC 
      LIMIT 5
    `).all();

    return {
      date: new Date().toISOString().split('T')[0],
      ...stats,
      popularPaths: topPaths
    };
  });

// Export app type for Eden client
export type App = typeof app;

// 서버 실행
if (import.meta.main) {
    app.listen(4000);
    console.log(
      `🦊 Backend-analytics (SQLite WAL) is running at ${app.server?.hostname}:${app.server?.port}`
    );
}