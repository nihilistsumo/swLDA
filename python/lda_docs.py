# -*- coding: utf-8 -*-
"""
Created on Wed Mar 16 16:48:50 2016

@author: sumanta
"""

from nltk.tokenize import RegexpTokenizer
from stop_words import get_stop_words
from gensim import corpora
import gensim.models.ldamodel as lda
import os, sqlite3, sys, threading, pickle

topic_num = 5
tokenizer = RegexpTokenizer(r'\w+')
en_stop = get_stop_words('en')
normal_texts=[]
# This variable controls whether it will use the semanticLDA algo or plain LDA
semanticLDA=True
def preprocess(text):
    raw = text.lower()
    tokens = tokenizer.tokenize(raw)
    # remove stop words from tokens
    stopped_tokens = [i for i in tokens if not i in en_stop]
    return stopped_tokens

with open('term_weight.pkl','rb') as tw:
    term_weight = pickle.load(tw)
with open('term_dict.pkl','rb') as td:
    term_dict = pickle.load(td)
with open('missing_words.pkl','rb') as mw:
    missing = pickle.load(mw)

corpus = []
doctokens = []
filenames = []
filetokens = {}
#input directory for uncategorized corpus
corpusdir="/home/sumanta/Documents/thesis/pytm/bbc_small/unsorted/"
for file in os.listdir(corpusdir):
    infile = open(corpusdir+file, 'r')
    try:
        for line in infile:
            for token in preprocess(line):
                if (semanticLDA):
                    if (term_weight[token][0] in filetokens):
                        filetokens[term_weight[token][0]] += round(term_weight[token][1])
                    else:
                        filetokens[term_weight[token][0]] = round(term_weight[token][1])
                else:
                    doctokens.append(token)
        normal_texts.append(doctokens)
        doctokens = []
        if (semanticLDA):
            corpus.append(list(filetokens.items()))
        else:
            dictionary = corpora.Dictionary(normal_texts)
            corpus = [dictionary.doc2bow(text) for text in normal_texts]
        filetokens.clear()
        filenames.append(file)
    except UnicodeDecodeError:
        print("Could not read character in "+str(file)+", skipping...")
        

ldamodel=lda.LdaModel(corpus, num_topics=topic_num, id2word=term_dict, passes=10)



#write dtmat in xls file
import xlsxwriter as xl

doc2show=100
dtmat=[[0 for j in range(topic_num)] for i in range(doc2show)]
for k in range(doc2show):
    topdist=ldamodel[corpus[k]]
    for val in topdist:
        dtmat[k][val[0]]=val[1]
wb=xl.Workbook('dtmat.xls')
ws=wb.add_worksheet('Documents')
row=0
fn=0
for doc in dtmat:
    col=0
    ws.write(row,col,filenames[fn])
    fn+=1
    col=1
    for top in doc:
        ws.write(row,col,top)
        col += 1
    row += 1
wstopic=wb.add_worksheet('Topics')
row=0
for n in range(topic_num):
    words=ldamodel.show_topic(n)
    col=0
    for m in range(len(words)):
        wstopic.write(row,col,words[m][0])
        col += 1
    row += 1
print("Closing worksheet...")
wb.close()
