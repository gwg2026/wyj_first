#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试编码修复后的评论功能
"""

import requests
import json

def test_encoding_fix():
    """测试编码修复"""
    print("🔧 开始测试编码修复")
    print("=" * 50)
    
    BASE_URL = "http://localhost:8082"
    
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
    
    # 3. 测试获取评论列表（检查编码）
    print("\n📋 Step 3: 获取评论列表（检查编码）...")
    comments_response = session.get(f"{BASE_URL}/lost/comments/1")
    print(f"获取评论状态码: {comments_response.status_code}")
    print(f"响应头 Content-Type: {comments_response.headers.get('Content-Type', 'N/A')}")
    
    if comments_response.status_code == 200:
        try:
            # 确保使用UTF-8编码解析响应
            comments_data = comments_response.json()
            print("✅ 评论数据解析成功")
            
            # 检查评论内容编码
            if comments_data.get('success') and comments_data.get('data'):
                comments_list = comments_data['data']
                print(f"📝 共获取到 {len(comments_list)} 条评论")
                
                for i, comment in enumerate(comments_list):
                    content = comment.get('content', '')
                    user_name = comment.get('userName', '')
                    print(f"\n评论 {i+1}:")
                    print(f"  用户: {user_name}")
                    print(f"  内容: {content}")
                    print(f"  内容长度: {len(content)}")
                    
                    # 检查是否还有乱码
                    if '?' in content and len(content) > 0:
                        print(f"  ⚠️  检测到可能的编码问题: {content}")
                    else:
                        print(f"  ✅ 编码正常")
                    
                    # 检查回复
                    if 'replies' in comment and comment['replies']:
                        print(f"  📍 回复数量: {len(comment['replies'])}")
                        for j, reply in enumerate(comment['replies']):
                            reply_content = reply.get('content', '')
                            print(f"    回复 {j+1}: {reply_content}")
                            if '?' in reply_content and len(reply_content) > 0:
                                print(f"      ⚠️  回复编码问题: {reply_content}")
                            else:
                                print(f"      ✅ 回复编码正常")
            else:
                print(f"❌ 评论数据格式异常: {comments_data}")
                
        except json.JSONDecodeError as e:
            print(f"❌ JSON解析错误: {e}")
            print(f"原始响应内容: {comments_response.text[:500]}")
    else:
        print(f"❌ 获取评论失败，状态码: {comments_response.status_code}")
    
    # 4. 测试创建新评论
    print("\n✍️  Step 4: 测试创建中文评论...")
    test_content = "这是一个测试评论 - 检查编码是否正常"
    comment_data = {
        'itemId': '1',
        'content': test_content
    }
    
    create_response = session.post(f"{BASE_URL}/lost/comment", data=comment_data)
    print(f"创建评论状态码: {create_response.status_code}")
    
    if create_response.status_code == 200:
        try:
            create_result = create_response.json()
            print(f"创建结果: {create_result}")
            if create_result.get('success'):
                print("✅ 中文评论创建成功")
            else:
                print(f"❌ 创建失败: {create_result.get('message', '未知错误')}")
        except json.JSONDecodeError:
            print(f"创建响应: {create_response.text}")
    else:
        print(f"❌ 创建评论失败，状态码: {create_response.status_code}")
    
    # 5. 重新获取评论验证
    print("\n🔍 Step 5: 重新获取评论验证...")
    comments_response2 = session.get(f"{BASE_URL}/lost/comments/1")
    
    if comments_response2.status_code == 200:
        try:
            comments_data2 = comments_response2.json()
            if comments_data2.get('success') and comments_data2.get('data'):
                comments_list2 = comments_data2['data']
                print(f"📝 更新后共有 {len(comments_list2)} 条评论")
                
                # 查找刚创建的评论
                for comment in comments_list2:
                    content = comment.get('content', '')
                    if test_content in content:
                        print(f"✅ 找到新创建的评论: {content}")
                        break
                else:
                    print("❌ 未找到新创建的评论")
        except json.JSONDecodeError:
            print(f"❌ JSON解析错误: {comments_response2.text[:200]}")
    
    print("\n" + "=" * 50)
    print("🎯 编码修复测试完成")

if __name__ == "__main__":
    test_encoding_fix()