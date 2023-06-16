# Freud

心理服务后端-弗洛伊德

## Getting Started

本地开发使用本地环境，在src/main/resources中新建application-local.yaml并配置数据库地址等信息。

微信小程序和腾讯IM必须配置

IDEA启动配置Active Profiles填写local, dev

```yaml
# example application-local.yaml
freud:
  weixin:
    app-id: 
    secret: 
  im:
    app-id: 
    secret-key: 
    token: 
  sms:
    secretId:
    secretKey:
    sdkAppId:
    signName:
    templateId:
```