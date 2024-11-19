import requests
import numpy as np
from gensim.models import FastText
import random


def extract_names(hashtags):
    result = []
    for hashtag in hashtags:
        hashtag_name = hashtag['name']
        if hashtag_name is not None:
            result.append(hashtag_name)
    return result

def train_and_save_fasttext_model(tags, model_path):
    model = FastText(sentences=tags, vector_size=100, window=5, min_count=1, workers=4)

    model.save(model_path)
    print(f"모델이 {model_path}에 저장되었습니다.")
    return model

def load_fasttext_model(model_path):
    model = FastText.load(model_path)
    return model

def get_similar_tags_for_review(model, user_viewed_tags, all_tags):
    review_tag_vectors = [model.wv[tag] for tag in user_viewed_tags if tag in model.wv]
    review_vector = np.mean(review_tag_vectors, axis=0) if review_tag_vectors else np.zeros(model.vector_size)

    similar_tags = {}
    for tag in all_tags:
        if tag in model.wv:
            tag_vector = model.wv[tag]
            similarity = cosine_similarity(review_vector, tag_vector)
            if tag in user_viewed_tags:
                similarity *= 0.1
            similar_tags[tag] = similarity

    return similar_tags

def cosine_similarity(vec1, vec2):
    dot_product = np.dot(vec1, vec2)
    norm_a = np.linalg.norm(vec1)
    norm_b = np.linalg.norm(vec2)
    return dot_product / (norm_a * norm_b) if norm_a > 0 and norm_b > 0 else 0.0

def sort_similar_tags(review_tags, model, all_tags, size):

    review_similar_tags = get_similar_tags_for_review(model, review_tags, all_tags)

    num_review_tags = int(size)
    top_review_tags = sorted(review_similar_tags.items(), key=lambda x: x[1], reverse=True)[:num_review_tags]

    top_review_tags_list = [tag for tag, _ in top_review_tags]

    return top_review_tags_list

def sort_similar_tag_random(review_tags, model, all_tags):

    review_similar_tags = get_similar_tags_for_review(model, review_tags, all_tags)

    top_review_tags = sorted(review_similar_tags.items(), key=lambda x: x[1], reverse=True)[:30]
    top_review_random_tag = random.choice(top_review_tags)[0]

    return top_review_random_tag


def get_all_tags_from_java():
    response = requests.get("http://localhost:8080/hashtag/getAllTags")
    if response.status_code == 200:
        return response.json()
    else:
        return []

def get_viewed_review_tags_from_java(access_token):
    headers = {
        "access": access_token
    }

    response = requests.get("http://localhost:8080/hashtag/getJwtMemberViewedReviewTags", headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        return []
