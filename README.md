# Vue Blog System

一个基于 Vue2 + Element UI 的博客系统前端项目。

## 功能特性

### 用户系统
- [x] 用户注册
- [x] 用户登录
- [x] 用户信息展示与编辑
- [x] Token 认证
- [x] 路由守卫

### 文章管理
- [x] 文章列表展示
- [x] 文章分页
- [x] 文章详情
- [x] 创建文章
- [x] 编辑文章
- [x] 删除文章
- [x] 我的文章管理
- [x] 文章点赞功能（防重复点赞）
- [x] 热门文章推荐

### 个人中心
- [x] 用户统计信息（文章数、获赞数、粉丝数）
- [x] 最近发布文章展示
- [x] 用户关注功能
- [x] 个人资料编辑

### 首页功能
- [x] 最新文章列表
- [x] 用户信息卡片
  - 显示用户基本信息
  - 显示统计数据（文章、获赞、粉丝）
  - 悬停展示最近发布文章
  - 快速访问个人文章列表

### UI/UX 优化
- [x] 响应式布局
- [x] 文章列表卡片式设计
- [x] 平滑的过渡动画
- [x] 用户友好的交互提示
- [x] 统一的错误处理

## 技术栈

- Vue 2.x
- Vuex
- Vue Router
- Element UI
- Axios
- Sa-Token（后端认证）

## 项目结构
tree
src/
├── api/ # API 接口
├── assets/ # 静态资源
├── components/ # 公共组件
├── router/ # 路由配置
├── store/ # Vuex 状态管理
├── utils/ # 工具函数
└── views/ # 页面组件

## 后端接口

### 用户相关
- POST `/api/user/login` - 用户登录
- POST `/api/user/register` - 用户注册
- GET `/api/user/info` - 获取用户信息
- PUT `/api/user/update` - 更新用户信息
- POST `/api/user/logout` - 退出登录

### 文章相关
- GET `/api/article/getArticles` - 获取文章列表
- GET `/api/article/getArticle/:id` - 获取文章详情
- GET `/api/article/getMyArticles` - 获取我的文章
- POST `/api/article/createArticle` - 创建文章
- PUT `/api/article/updateArticle/:id` - 更新文章
- DELETE `/api/article/deleteArticle/:id` - 删除文章
- GET `/api/article/getLikeStatus/:id` - 获取点赞状态
- POST `/api/article/likeArticle/:id` - 点赞/取消点赞
- GET `/api/article/hotArticles` - 获取热门文章

### 作者相关
- GET `/api/author/info/:id` - 获取作者信息
- POST `/api/author/:id/follow` - 关注/取消关注作者

## 数据库表结构

### 用户表 (user)
- id: 用户ID
- username: 用户名
- password: 密码
- email: 邮箱
- avatar: 头像
- create_time: 创建时间
- update_time: 更新时间
- deleted: 是否删除

### 文章表 (article)
- id: 文章ID
- user_id: 作者ID
- title: 标题
- content: 内容
- summary: 摘要
- views: 浏览量
- likes: 点赞数
- create_time: 创建时间
- update_time: 更新时间
- deleted: 是否删除

### 用户关注表 (user_follow)
- id: 关系ID
- user_id: 关注者ID
- follow_id: 被关注者ID
- create_time: 创建时间

### 文章点赞表 (article_like)
- id: 点赞ID
- article_id: 文章ID
- user_id: 用户ID
- create_time: 创建时间

## 安装和运行

#安装依赖
npm install
#开发环境运行
npm run serve
#生产环境构建
npm run build
