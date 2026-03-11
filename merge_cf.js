const fs = require('fs');
const config = JSON.parse(fs.readFileSync('full-config-clean.json', 'utf8'));

// OAI ARN
const oai = 'origin-access-identity/cloudfront/EMVUIP3TD3290';

// 1. Update Origins
config.DistributionConfig.Origins.Quantity = 4;
const items = config.DistributionConfig.Origins.Items;

// Update HostWeb OAI
const hostWeb = items.find(i => i.Id === 'HostWeb');
if (hostWeb) {
  hostWeb.S3OriginConfig.OriginAccessIdentity = oai;
}

// Add AssetsS3 if not exists
if (!items.find(i => i.Id === 'AssetsS3')) {
  items.push({
    Id: 'AssetsS3',
    DomainName: 'nextstay-assets-281414549133.s3.ap-northeast-2.amazonaws.com',
    OriginPath: '',
    CustomHeaders: { Quantity: 0 },
    S3OriginConfig: { OriginAccessIdentity: oai, OriginReadTimeout: 30 },
    ConnectionAttempts: 3,
    ConnectionTimeout: 10,
    OriginShield: { Enabled: false },
    OriginAccessControlId: ''
  });
}

// 2. Update Cache Behaviors
config.DistributionConfig.CacheBehaviors.Quantity = 4;
const behaviors = config.DistributionConfig.CacheBehaviors.Items;

// Add /_nuxt/* if not exists
if (!behaviors.find(b => b.PathPattern === '/_nuxt/*')) {
  behaviors.push({
    PathPattern: '/_nuxt/*',
    TargetOriginId: 'AssetsS3',
    TrustedSigners: { Enabled: false, Quantity: 0 },
    TrustedKeyGroups: { Enabled: false, Quantity: 0 },
    ViewerProtocolPolicy: 'redirect-to-https',
    AllowedMethods: {
      Quantity: 2,
      Items: ['HEAD', 'GET'],
      CachedMethods: { Quantity: 2, Items: ['HEAD', 'GET'] }
    },
    SmoothStreaming: false,
    Compress: true,
    LambdaFunctionAssociations: { Quantity: 0 },
    FunctionAssociations: { Quantity: 0 },
    FieldLevelEncryptionId: '',
    GrpcConfig: { Enabled: false },
    ForwardedValues: {
      QueryString: false,
      Cookies: { Forward: 'none' },
      Headers: { Quantity: 0 },
      QueryStringCacheKeys: { Quantity: 0 }
    },
    MinTTL: 0,
    DefaultTTL: 86400,
    MaxTTL: 31536000
  });
}

fs.writeFileSync('final-config.json', JSON.stringify(config.DistributionConfig, null, 2), 'utf8');
console.log('Final ETag:', config.ETag);
