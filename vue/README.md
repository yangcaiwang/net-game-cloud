## 开发

```bash

# 安装node.js@v16.20.2(ps:17及以上版本不兼容vue@2.6.12)
https://blog.csdn.net/weixin_42474607/article/details/140769161
# 进入项目目录
cd gamer-ui

# 安装依赖(windows下一定以管理员模式运行cmd)
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npmmirror.com

# 启动服务
npm run dev
```

浏览器访问 http://localhost:81

## 发布

```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod
```