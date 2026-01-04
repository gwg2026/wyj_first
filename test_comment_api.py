#!/usr/bin/env python3
"""
评论和回复功能的端到端测试脚本
"""
import requests
import json
import time

# 基础URL
BASE_URL = "http://localhost:8082"

def test_login():
    """测试用户登录"""
    print("🔐 测试用户登录...")
    
    # 登录数据
    login_data = {
        'username': 'admin',
        'password': 'admin123'
    }
    
    # 创建session来保持登录状态
    session = requests.Session()
    
    # 设置会话保持和重定向处理
    session.allow_redirects = True
    
    # 登录
    login_response = session.post(f"{BASE_URL}/user/login", data=login_data, allow_redirects=True)
    print(f"登录状态码: {login_response.status_code}")
    print(f"登录重定向URL: {login_response.url}")
    
    # 检查登录是否成功 - 通过检查session中是否包含用户信息
    # 或者检查是否成功重定向到登录后的页面
    if login_response.status_code == 200 and ("lost/list" in login_response.url or "lost/detail" in login_response.url):
        print("✅ 用户登录成功")
        return session
    else:
        print("❌ 用户登录失败")
        return None

def test_add_comment(session):
    """测试添加评论"""
    print("\n📝 测试添加评论...")
    
    # 添加评论数据
    comment_data = {
        'itemId': 1,
        'content': '这是测试评论功能，验证评论添加是否正常工作。'
    }
    
    # 发送评论请求
    comment_response = session.post(f"{BASE_URL}/lost/comment", data=comment_data)
    print(f"添加评论状态码: {comment_response.status_code}")
    print(f"添加评论响应: {comment_response.text}")
    
    try:
        result = comment_response.json()
        if result.get('success'):
            print("✅ 评论添加成功")
            return True
        else:
            print(f"❌ 评论添加失败: {result.get('message')}")
            return False
    except:
        print("❌ 响应格式错误")
        return False

def test_get_comments(session):
    """测试获取评论列表"""
    print("\n📋 测试获取评论列表...")
    
    # 获取评论列表
    comments_response = session.get(f"{BASE_URL}/lost/comments/1")
    print(f"获取评论状态码: {comments_response.status_code}")
    
    try:
        result = comments_response.json()
        if result.get('success'):
            comments = result.get('comments', [])
            print(f"✅ 获取评论列表成功，共 {len(comments)} 条评论")
            
            # 打印评论详情
            for i, comment in enumerate(comments, 1):
                print(f"  评论 {i}: {comment.get('content', '')} (作者: {comment.get('userName', '匿名')})")
                replies = comment.get('replies', [])
                for j, reply in enumerate(replies, 1):
                    print(f"    回复 {j}: {reply.get('content', '')} (作者: {reply.get('userName', '匿名')})")
            
            return comments
        else:
            print("❌ 获取评论列表失败")
            return []
    except:
        print("❌ 响应格式错误")
        return []

def test_comment_reply(session, comment_id):
    """测试评论回复"""
    print(f"\n💬 测试评论回复 (评论ID: {comment_id})...")
    
    # 回复数据
    reply_data = {
        'commentId': comment_id,
        'content': '这是测试回复，验证回复功能是否正常工作。'
    }
    
    # 发送回复请求
    reply_response = session.post(f"{BASE_URL}/lost/comment/reply", data=reply_data)
    print(f"回复状态码: {reply_response.status_code}")
    print(f"回复响应: {reply_response.text}")
    
    try:
        result = reply_response.json()
        if result.get('success'):
            print("✅ 评论回复成功")
            return True
        else:
            print(f"❌ 评论回复失败: {result.get('message')}")
            return False
    except:
        print("❌ 响应格式错误")
        return False

def test_comment_like(session, comment_id):
    """测试评论点赞"""
    print(f"\n👍 测试评论点赞 (评论ID: {comment_id})...")
    
    # 点赞数据
    like_data = {
        'commentId': comment_id
    }
    
    # 发送点赞请求
    like_response = session.post(f"{BASE_URL}/lost/comment/like", data=like_data)
    print(f"点赞状态码: {like_response.status_code}")
    print(f"点赞响应: {like_response.text}")
    
    try:
        result = like_response.json()
        if result.get('success'):
            like_count = result.get('likeCount', 0)
            print(f"✅ 评论点赞成功，当前点赞数: {like_count}")
            return True
        else:
            print(f"❌ 评论点赞失败: {result.get('message')}")
            return False
    except:
        print("❌ 响应格式错误")
        return False

def main():
    """主测试函数"""
    print("🚀 开始评论和回复功能的端到端测试")
    print("=" * 50)
    
    # 1. 测试登录
    session = test_login()
    if not session:
        print("❌ 登录失败，终止测试")
        return
    
    # 2. 测试添加评论
    if not test_add_comment(session):
        print("❌ 添加评论失败，但继续测试其他功能")
    
    # 3. 测试获取评论列表
    comments = test_get_comments(session)
    
    # 4. 如果有评论，测试回复和点赞
    if comments:
        first_comment = comments[0]
        comment_id = first_comment.get('id')
        
        if comment_id:
            # 测试回复
            test_comment_reply(session, comment_id)
            
            # 测试点赞
            test_comment_like(session, comment_id)
            
            # 再次获取评论列表，查看更新后的状态
            print("\n📋 再次获取评论列表，查看更新后的状态...")
            updated_comments = test_get_comments(session)
    else:
        print("⚠️  没有找到评论，跳过回复和点赞测试")
    
    print("\n" + "=" * 50)
    print("🎉 评论和回复功能测试完成")

if __name__ == "__main__":
    main()