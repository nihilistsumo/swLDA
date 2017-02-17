# -*- coding: utf-8 -*-
"""
Created on Sun Apr 10 15:20:05 2016

@author: sumanta
"""

import pickle
import json

with open('term_dict.pkl','rb') as td:
    term_dict = pickle.load(td)
    td.close()
vals = []
for t in term_dict.values():
    vals.append(t)
with open('terms_arr.pkl','wb') as tdarr:
    pickle.dump(vals, tdarr, protocol=2)
    tdarr.close()
with open('terms_arr.json','w') as jsonarr:
    json.dump(vals, jsonarr)
    jsonarr.close()
