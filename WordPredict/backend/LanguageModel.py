from typing import NoReturn, List
from pathlib import Path
import json


class LanguageModel(object):

    def __init__(self, model_path: Path):
        super().__init__()
        self.__predict_dict = {}
        self.__load(model_path)

    def __load(self, path: Path) -> NoReturn:
        with open(path, 'r', encoding='utf-8') as f:
            for line in f:
                try:
                    line_list = line.strip('\n').split('\t', 2)
                    prefix = line_list[0]
                    suggest_dict = json.loads(line_list[1])
                    suggest_list = [k for k, _ in sorted(suggest_dict.items(), key=lambda kv:(kv[1], kv[0]), reverse=True)]
                    self.__predict_dict[prefix] = suggest_list[:20] if len(suggest_list) > 20 else suggest_list
                except Exception as err:
                    print(err)
                    pass

    def getSuggestList(self, prefix: str) -> List[str]:
        return self.__predict_dict.get(prefix, [])


if __name__ == '__main__':
    lm = LanguageModel(Path("./output/small_sample"))
    print(lm.getSuggestList("段经"))
