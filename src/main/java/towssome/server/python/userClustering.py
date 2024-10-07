import subprocess
import sys

libraries = [
    'numpy',
    'gensim',
    'sklearn',
    'requests',
    'flask',
]

for library in libraries:
    try:
        __import__(library)
    except ImportError:
        print(f"Not exist {library}. installing...")
        subprocess.check_call([sys.executable, '-m', 'pip', 'install', library])

import requests
import numpy as np
#from gensim.models import Word2Vec
from gensim.models import FastText

from sklearn.preprocessing import StandardScaler
from flask import Flask, jsonify

#from sklearn.cluster import DBSCAN
#from sklearn.neighbors import NearestNeighbors
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score


def fetch_user_tags():
    response = requests.get("http://localhost:8080/allmemberprofiletag")
    if response.status_code == 200:
        return response.json()
    else:
        return None

"""
#Word2Vec
def vectorize_tags(user_tags):
    all_tags = [tag for user in user_tags.values() for tag in user["tags"]]

    # Word2Vec 모델 훈련
    model = Word2Vec([all_tags], vector_size=100, window=5, min_count=1, workers=4)

    # 태그 벡터 생성
    user_vectors = {}
    for user_id, user in user_tags.items():
        tags = user["tags"]
        vectors = [model.wv[tag] for tag in tags if tag in model.wv]
        if vectors:
            user_vectors[user_id] = np.mean(vectors, axis=0)

    return user_vectors
"""

#FastWord
def vectorize_tags(user_tags):
    all_tags = [tag for user in user_tags.values() for tag in user["tags"]]

    # FastText 모델 훈련
    model = FastText(sentences=[all_tags], vector_size=100, window=5, min_count=1, workers=4)

    # 태그 벡터 생성
    user_vectors = {}
    for user_id, user in user_tags.items():
        tags = user["tags"]
        vectors = [model.wv[tag] for tag in tags if tag in model.wv]
        if vectors:
            user_vectors[user_id] = np.mean(vectors, axis=0)

    return user_vectors


# K-Means 클러스터링 함수
def cluster_users(user_vectors):
    vectors = np.array(list(user_vectors.values()))

    # 데이터 스케일링
    scaler = StandardScaler()
    scaled_vectors = scaler.fit_transform(vectors)

    # 현재 사용자의 수에 따라 max_clusters 설정
    num_users = len(user_vectors)
    max_clusters = num_users // 3 if num_users >= 3 else 1  # 최소 1개의 클러스터 보장
    best_k = 5  # 최소 클러스터 수
    best_score = -1  # Silhouette 점수 초기값
    best_model = None

    for k in range(best_k, max_clusters + 1):
        kmeans = KMeans(n_clusters=k, random_state=42)
        labels = kmeans.fit_predict(scaled_vectors)

        # Silhouette 점수 계산
        score = silhouette_score(scaled_vectors, labels)
        print(f"K={k}, Silhouette Score: {score}")

        if score > best_score:
            best_score = score
            best_k = k
            best_model = kmeans

    print(f"Best K: {best_k}, Best Silhouette Score: {best_score}")

    # 클러스터 레이블 확인
    clustered_users = {}
    for user_id, cluster_id in zip(user_vectors.keys(), best_model.labels_):
        if cluster_id not in clustered_users:
            clustered_users[cluster_id] = []
        clustered_users[cluster_id].append(user_id)

    return clustered_users



"""

# DBSCAN
def cluster_users(user_vectors):
    # 벡터를 리스트로 변환
    vectors = np.array(list(user_vectors.values()))

    # KNN 거리 계산
    neighbors = NearestNeighbors(n_neighbors=5)
    neighbors_fit = neighbors.fit(vectors)
    distances, _ = neighbors_fit.kneighbors(vectors)

    # 거리 정렬 및 eps 계산을 최대 거리로 설정
    distances = np.sort(distances[:, 4])  # 4는 K-최근접 이웃의 거리
    optimal_eps = np.max(distances) * 100

    min_samples = 1

    # 데이터 스케일링
    scaler = StandardScaler()
    scaled_vectors = scaler.fit_transform(vectors)

    # DBSCAN 클러스터링
    clustering = DBSCAN(eps=optimal_eps, min_samples=min_samples).fit(scaled_vectors)


    print(f"Cluster labels: {clustering.labels_}")


    clustered_users = {}
    for user_id, cluster_id in zip(user_vectors.keys(), clustering.labels_):
        if cluster_id not in clustered_users:
            clustered_users[cluster_id] = []
        clustered_users[cluster_id].append(user_id)

    return clustered_users

"""
