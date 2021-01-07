from flask import Flask, request
from flask_cors import CORS
import json
from pathlib import Path

from LanguageModel import LanguageModel

RESULT_ERR = 1
RESULT_OK = 0
MSG_INTERNAL_ERROR = 'Internal error.'
MSG_OK = 'ok'

# Flask App
app = Flask(__name__)
lm = LanguageModel(Path("./output/part-r-00000"))


@app.route('/suggest', methods=['POST'])
def suggest():
    '''
    根据前缀进行输入预测，返回预测的下一个字符列表
    '''
    data = json.loads(request.data)
    try:
        res_list = lm.getSuggestList(data['prefix'])

    except Exception as err:
        print(err)
        return json.dumps({'result': RESULT_ERR, 'msg': MSG_INTERNAL_ERROR, 'data': None}, ensure_ascii=False)

    reply = {
        'result': RESULT_OK,
        'msg': MSG_OK,
        'data': res_list
    }
    return json.dumps(reply, ensure_ascii=False)


if __name__ == '__main__':
    CORS(app, support_credentials=True)
    app.run('0.0.0.0', threaded=False)
