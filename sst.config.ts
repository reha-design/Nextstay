/// <reference path="./.sst/platform/config.d.ts" />

export default $config({
  app(input) {
    return {
      name: "nextstay",
      removal: input?.stage === "production" ? "retain" : "remove",
      protect: ["production"].includes(input?.stage),
      home: "aws",
      providers: {
        aws: {
          region: "ap-northeast-2",
        },
      },
    };
  },
  async run() {
    // 1. 호스트 웹 (S3 + CloudFront 정적 사이트) 배포
    // CSR 방식의 호스트 웹을 위한 S3 버킷 및 CloudFront 설정
    const hostWeb = new sst.aws.StaticSite("HostWeb", {
      path: "frontend-backoffice", // 호스트 웹 프로젝트 경로
      build: {
        command: "npm run build", // 빌드 명령어
        output: "dist", // 빌드 결과물 경로
      },
    });

    // 2. 게스트용 Nuxt SSR (Lambda Function URL 기반) 배포
    // 비용 최적화를 위해 API Gateway 대신 Function URL 사용
    const guestSSR = new sst.aws.Nuxt("GuestSSR", {
      path: "frontend-guest",
      environment: {
        NUXT_PUBLIC_API_URL: "http://ec2-3-35-10-21.ap-northeast-2.compute.amazonaws.com",
        NUXT_PUBLIC_ANALYTICS_URL: "http://ec2-3-35-10-21.ap-northeast-2.compute.amazonaws.com",
      }
    });

    // 3. 자산 서빙 최적화 (S3 + CloudFront)
    // sst.aws.Nuxt는 자동으로 S3와 CloudFront를 구성하여 정적 자산을 효율적으로 서빙함

    return {
      hostWebUrl: hostWeb.url,
      guestSsrUrl: guestSSR.url, // Lambda Function URL
    };
  },
});
