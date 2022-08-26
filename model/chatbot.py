import pandas as pd
import torch
import random
import os
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

os.chdir(os.path.dirname(os.path.abspath(__file__)))


class Chatbot:

    def __init__(self):
        if torch.cuda.is_available():
            self.chatbot = SentenceTransformer('jhgan/ko-sroberta-multitask', device='cuda:0')
        else:
            self.chatbot = SentenceTransformer('jhgan/ko-sroberta-multitask')
        
        self.dataset = pd.read_csv('./data/dataset.csv')
        self.dataset['embedding'] = self.dataset.Q.map(lambda question: list(self.chatbot.encode(question)))
    
    def predict(self, question):
        encoded = self.chatbot.encode(question)
        self.dataset['similarity'] = self.dataset.embedding.map(lambda sim: cosine_similarity([encoded], [sim]).squeeze())
        
        answers = self.dataset[self.dataset.similarity == self.dataset.similarity.max()]
        answer = answers.iloc[random.randint(0, answers.shape[0] - 1)]
        
        return answer.A