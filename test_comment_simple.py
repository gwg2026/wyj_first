#!/usr/bin/env python3
"""
评论功能测试脚本 - 简化版本
"""
import requests
import json
import time

# 基础URL
BASE_URL = "http://localhost:8082"

def test_comment_flow():
    """测试评论流程"""
    print("🚀 开始评论功能测试")
    print("=" * 50)
    
    # 创建session来保持登录状态
    session = requests.Session()
    
    # 1. 先访问登录页面获取会话
    print("🔐 Step 1: 访问登录页面...")
    login_page_response = session.get(f"{BASE_URL}/user/login")
    print(f"登录页面状态码: {login_page_response.status_code}")
    
    # 2. 模拟登录
    print("🔐 Step 2: 用户登录...")
    login_data = {
        'username': 'admin',
        'password': 'admin123'
    }
    
    login_response = session.post(f"{BASE_URL}/user/login", data=login_data)
    print(f"登录状态码: {login_response.status_code}")
    print(f"登录后URL: {login_response.url}")
    
    # 3. 测试获取评论列表（无需登录即可查看）
    print("\n📋 Step 3: 获取评论列表...")
    comments_response = session.get(f"{BASE_URL}/lost/comments/1")
    print(f"获取评论状态码: {comments_response.status_code}")
    
    if comments_response.status_code == 200:
        try:
            result = comments_response.json()
            if result.get('success'):
                comments = result.get('comments', [])
                print(f"✅ 获取评论列表成功，共 {len(comments)} 条评论")
                
                # 打印评论详情
                for i, comment in enumerate(comments, 1):
                    print(f"  评论 {i}: {comment.get('content', '')}")
                    print(f"    作者: {comment.get('userName', '匿名')}")
                    print(f"    点赞数: {comment.get('likes', 0)}")
                    print(f"    创建时间: {comment.get('createTime', '')}")
                    
                    replies = comment.get('replies', [])
                    for j, reply in enumerate(replies, 1):
                        print(f"    回复 {j}: {reply.get('content', '')} (作者: {reply.get('userName', '匿名')})")
                    print()
                
                return session, comments
            else:
                print(f"❌ 获取评论列表失败: {result.get('message', '未知错误')}")
                return session, []
        except Exception as e:
            print(f"❌ 解析响应失败: {e}")
            print(f"原始响应: {comments_response.text}")
            return session, []
    else:
        print(f"❌ 获取评论列表失败，状态码: {comments_response.status_code}")
        return session, []

def test_add_comment_manual(session):
    """手动测试添加评论"""
    print("\n📝 Step 4: 测试添加评论...")
    
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
    except Exception as e:
        print(f"❌ 解析响应失败: {e}")
        return False

def main():
    """主测试函数"""
    try:
        # 执行评论流程测试
        session, comments = test_comment_flow()
        
        # 如果有评论数据，测试添加评论
        if session:
            test_add_comment_manual(session)
        
        print("\n" + "=" * 50)
        print("🎉 评论功能测试完成")
        
    except Exception as e:
        print(f"❌ 测试过程中发生错误: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()