from nltk.tokenize import RegexpTokenizer
from stop_words import get_stop_words
from gensim import corpora
#import gensim.models.ldamodel as lda
import os, sqlite3, sys, threading, pickle, math

tokenizer = RegexpTokenizer(r'\w+')
en_stop = get_stop_words('en')
dbdir = "/home/sumanta/"
db = "ldadb"
missing = []
missingLock = threading.Lock()
term_wt_dict = {}
MAX_THREADS = 100
MIN_THREADS = 50
checked = False
scale = 1

#corpus path
corpusdir="/home/sumanta/Documents/thesis/pytm/bbc_small/unsorted/"
    
def preprocess(text):
    raw = text.lower()
    tokens = tokenizer.tokenize(raw)
    # remove stop words from tokens
    stopped_tokens = [i for i in tokens if not i in en_stop]
    return stopped_tokens
#def expensive_op(word):
#change this expensive_op to make use of the weight calculated from babelnet
def expensive_op(id):
    try:
        word = dictnry.get(id)
        conn=sqlite3.connect(dbdir+db)
        c=conn.cursor()
        c.execute("select weight from vocab3 where word='%s'" % word)
        row=c.fetchone()
        if row is not None:
            raw_w=row[0]
            #print("wmax = "+str(wmax)+" wmin = "+str(wmin))
            w=scale*(raw_w - wmin)/(wmax - wmin)
        else:
            w=0
            with missingLock:
                missing.append(word)
        conn.close()
        term_wt_dict[word] = (id, w)
    except:
        e=sys.exc_info()[0]
        print("\nError occurred :"+str(e))
# This calculates term weights from dictionary
# cal_score(frequency, parts of speech) returns scaling_factor*weight
#this is not used anymore
def cal_score(fr,pos):
    scale = 3
    rt=1/(math.log(fr)+1);
    if ((pos=='nn0')or(pos=='nn1')or(pos=='nn2')or(pos=='np0')or(pos=='nn1-np0')):
        wt=rt*5
    elif pos[:2]=='vv':
        wt=rt*3
    elif ((len(pos)==3)and(pos[:2]=='aj')):
        wt=rt*2
    elif pos=='av0':
        wt=rt*2
    else:
        wt=0
    return scale*wt
tokens = []
try:
    conn=sqlite3.connect(dbdir+db)
    c=conn.cursor()
    #vocab3 is the database name where the term -> score mapping is stored
    c.execute("select max(weight),min(weight) from vocab3")
    r=c.fetchone()
    wmax=r[0]
    wmin=r[1]
    #print("max weight = "+str(wmax)+", min weight = "+str(wmin))
    conn.close()
except:
    e=sys.exc_info()[0]
    print("\nError occurred :"+str(e))
for file in os.listdir(corpusdir):
    infile = open(corpusdir+file, 'r')
    filetokens = []
    try:
        for line in infile:
            for token in preprocess(line):
                filetokens.append(token)
        tokens.append(filetokens)
    except UnicodeDecodeError:
        print("Could not read character in "+str(file)+", skipping...")

dictnry = corpora.Dictionary(tokens)
token_num = len(dictnry.keys())
precorpus=[dictnry.doc2bow(t) for t in tokens]
corpus = []
mythreads = []
for id in dictnry.keys():
    t=threading.Thread(target=expensive_op, args=(id, ))
    mythreads.append(t)
    t.start()
    perc=len(term_wt_dict)*100/token_num
    prog="\r["+"#"*round(perc/2)+"-"*round((100-perc)/2)+"] Completed "+"{0:.2f}".format(perc)+"%"
    sys.stdout.write(prog)
    sys.stdout.flush()
    if (len(mythreads)>MAX_THREADS):
        for threads in mythreads:
            threads.join()
            if (len(mythreads)<(MIN_THREADS+1)):
                break
for threads in mythreads:
    threads.join()
print("\nCompleted")
with open('term_weight.pkl','wb') as outfile:
    pickle.dump(term_wt_dict, outfile, pickle.HIGHEST_PROTOCOL)
with open('term_dict.pkl','wb') as term_dict:
    pickle.dump(dictnry, term_dict, pickle.HIGHEST_PROTOCOL)
with open('missing_words.pkl','wb') as missing_list:
    pickle.dump(missing, missing_list, pickle.HIGHEST_PROTOCOL)
print("List of missing words :"+str(missing))
print(str(len(missing)*100/token_num)+"% words are missing from database")
