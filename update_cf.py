import boto3
import json

client = boto3.client('cloudfront')
dist_id = 'E3H9ZLPHTJ0JRX'

# 1. Get current config
response = client.get_distribution_config(Id=dist_id)
config = response['DistributionConfig']
etag = response['ETag']

# 2. Update Origins
oai_id = 'origin-access-identity/cloudfront/EMVUIP3TD3290'

# Ensure 4 origins
origins = config['Origins']
items = origins['Items']

# Add AssetsS3 if missing
if not any(o['Id'] == 'AssetsS3' for o in items):
    items.append({
        'Id': 'AssetsS3',
        'DomainName': 'nextstay-assets-281414549133.s3.ap-northeast-2.amazonaws.com',
        'S3OriginConfig': {
            'OriginAccessIdentity': oai_id
        }
    })
    origins['Quantity'] = len(items)

# Update S3 OAIs
for o in items:
    if o['Id'] in ['HostWeb', 'AssetsS3']:
        if 'S3OriginConfig' in o:
            o['S3OriginConfig']['OriginAccessIdentity'] = oai_id

# 3. Update Cache Behaviors
behaviors = config['CacheBehaviors']
b_items = behaviors['Items']

# Add /_nuxt/* if missing
if not any(b['PathPattern'] == '/_nuxt/*' for b in b_items):
    b_items.append({
        'PathPattern': '/_nuxt/*',
        'TargetOriginId': 'AssetsS3',
        'ForwardedValues': {
            'QueryString': False,
            'Cookies': {'Forward': 'none'},
            'Headers': {'Quantity': 0},
            'QueryStringCacheKeys': {'Quantity': 0}
        },
        'TrustedSigners': {'Enabled': False, 'Quantity': 0},
        'ViewerProtocolPolicy': 'redirect-to-https',
        'MinTTL': 0,
        'AllowedMethods': {
            'Quantity': 2,
            'Items': ['HEAD', 'GET'],
            'CachedMethods': {'Quantity': 2, 'Items': ['HEAD', 'GET']}
        },
        'SmoothStreaming': False,
        'DefaultTTL': 86400,
        'MaxTTL': 31536000,
        'Compress': True,
        'LambdaFunctionAssociations': {'Quantity': 0},
        'FunctionAssociations': {'Quantity': 0},
        'FieldLevelEncryptionId': ''
    })
    behaviors['Quantity'] = len(b_items)

# 4. Update
print(f"Updating distribution {dist_id} with ETag {etag}")
client.update_distribution(
    DistributionConfig=config,
    Id=dist_id,
    IfMatch=etag
)
print("Update successful!")
