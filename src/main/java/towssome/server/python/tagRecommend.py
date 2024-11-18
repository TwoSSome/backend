import requests
import numpy as np
from gensim.models import FastText


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

def get_similar_tags_for_search(model, search_term, all_tags):
    if search_term in model.wv:
        search_term_vector = model.wv[search_term]
    else:
        print(f"경고: '{search_term}' 검색어가 모델에 없습니다. 벡터를 학습합니다.")

        model.build_vocab([search_term], update=True)  # 기존 모델에 단어 추가
        model.train([search_term], total_examples=1, epochs=10)  # 단어에 대해 학습 (한 번만)

        search_term_vector = model.wv[search_term]

    similar_tags = {}
    for tag in all_tags:
        if tag in model.wv:
            tag_vector = model.wv[tag]
            similarity = cosine_similarity(search_term_vector, tag_vector)
            similar_tags[tag] = similarity

    return similar_tags

def cosine_similarity(vec1, vec2):
    dot_product = np.dot(vec1, vec2)
    norm_a = np.linalg.norm(vec1)
    norm_b = np.linalg.norm(vec2)
    return dot_product / (norm_a * norm_b) if norm_a > 0 and norm_b > 0 else 0.0

def combine_and_sort_similar_tags(review_tags, search_term, model, all_tags, size):

    review_similar_tags = get_similar_tags_for_review(model, review_tags, all_tags)

    search_similar_tags = get_similar_tags_for_search(model, search_term, all_tags)

    num_search_tags = int(size * 0.4)
    top_search_tags = sorted(search_similar_tags.items(), key=lambda x: x[1], reverse=True)[:num_search_tags]

    top_search_tag_keys = {tag[0] for tag in top_search_tags}

    num_review_tags = size - len(top_search_tags)
    sorted_review_tags = sorted(review_similar_tags.items(), key=lambda x: x[1], reverse=True)

    top_review_tags = []
    for tag, score in sorted_review_tags:
        if tag not in top_search_tag_keys:
            top_review_tags.append((tag, score))
            if len(top_review_tags) == num_review_tags:
                break

    combined_tags = []
    combined_tags.extend([tag for tag, _ in top_search_tags])
    combined_tags.extend([tag for tag, _ in top_review_tags])

    return combined_tags


def get_all_tags_from_java():
    response = requests.get("http://localhost:8080/hashtag/getAllTags")
    if response.status_code == 200:
        return response.json()
    else:
        return []

def get_viewed_review_tags_from_java():
    response = requests.get(f"http://localhost:8080/hashtag/getJwtMemberViewedReviewTags")
    if response.status_code == 200:
        return response.json()
    else:
        return []
