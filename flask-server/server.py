# -*- coding: utf-8 -*-\

from flask import Flask, request
import json, re, random
from flask_cors import CORS

app = Flask(__name__)
CORS(app)
with open('./triangle_solutions.edn','r') as file:
    dat = file.read()
games =  re.findall("\[[0-2\" ]+\]", dat)
games = [json.loads(x.replace(' ',',')) for x in games]

#returns A solved game
@app.route("/<game>", methods=["GET"])
def get_edn(game):
    print("input game: " , game)
    possible_games = [x for x in games if x[0] == game]
    next_moves = possible_games[random.randint(0, len(possible_games))]
    print(next_moves)
    return(','.join(next_moves), 200, {'Allow-Control-Allow-Credentials':'true'})
    
    
if __name__ == '__main__':
        app.run(threaded=True)

