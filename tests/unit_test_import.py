# encoding=utf8

import json
import re
import subprocess
import sys
import urllib.error
import urllib.parse
import urllib.request

API_BASEURL = "http://localhost:8080"

ROOT_ID = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"

IMPORT_BATCHES = [
    {
        "items": [
            {
                "type": "FOLDER",
                "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
    ,
    {
        "items": [
            {
                "type": "FOLDER",
                "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            },
            {
                "type": "FILE",
                "url": "/file/url1",
                "id": "863e1a7a-1304-42ae-943b-179184c077e3",
                "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "size": 128
            },
            {
                "type": "FILE",
                "url": "/file/url2",
                "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "size": 256
            }
        ],
        "updateDate": "2022-02-02T12:00:00Z"
    },
    {
        "items": [
            {
                "type": "FOLDER",
                "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            },
            {
                "type": "FILE",
                "url": "/file/url3",
                "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
                "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "size": 512
            },
            {
                "type": "FILE",
                "url": "/file/url4",
                "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "size": 1024
            }
        ],
        "updateDate": "2022-02-03T12:00:00Z"
    },
    {
        "items": [
            {
                "type": "FILE",
                "url": "/file/url5",
                "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "size": 64
            }
        ],
        "updateDate": "2022-02-03T15:00:00Z"
    }
]

IMPORT_BATCHES_1 = [
    {#- id каждого элемента является уникальным среди остальных элементов
        "items": [
            {
                "type": "FOLDER",
                "id": "1",
                "parentId": None
            },
            {
                "type": "FOLDER",
                "id": "1",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    },
    {#- id каждого элемента является уникальным среди остальных элементов
        "items": [
            {
                "type": "FOLDER",
                "id": "2",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]

IMPORT_BATCHES_2 = [
    {#- поле id не может быть равно null
        "items": [
            {
                "type": "FOLDER",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]

IMPORT_BATCHES_3 = [
    {#- родителем элемента может быть только папка
        "items": [
            {
                "type": "FILE",
                "id": "3",
                "parentId": None,
                "size": 64
            },
            {
                "type": "FILE",
                "id": "4",
                "parentId": "3",
                "size": 64
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]

IMPORT_BATCHES_4 = [
    {#- поле url при импорте папки всегда должно быть равно null
        "items": [
            {
                "type": "FOLDER",
                "id": "5",
                "parentId": None,
                "url": "nxhoeri"
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    },
    {
        "items": [
            {
               "type": "FOLDER",
               "id": "5",
               "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]

IMPORT_BATCHES_5 = [
    {#- размер поля url при импорте файла всегда должен быть меньше либо равным 255
        "items": [
            {
                "type": "FILE",
                "id": "6",
                "parentId": None,
                "size": 64,
                "url":"размер поля url при импорте файла всегда должен быть меньше либо равным 255размер поля url при импорте файла всегда должен быть меньше либо равным 255размер поля url при импорте файла всегда должен быть меньше либо равным 255размер поля url при импорте файла всегда должен быть меньше либо равным 255"
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]



IMPORT_BATCHES_6 = [
    {#- поле size при импорте папки всегда должно быть равно null
        "items": [
            {
                "type": "FOLDER",
                "id": "7",
                "parentId": None,
                "size": 3
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    },
    {
        "items": [
            {
               "type": "FOLDER",
               "id": "7",
               "parentId": None,
               "size": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]

IMPORT_BATCHES_7 = [
    {#- поле size для файлов всегда должно быть больше 0
        "items": [
            {
                "type": "FILE",
                "id": "8",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    },
    {
        "items": [
            {
                "type": "FILE",
                "id": "8",
                "parentId": None,
                "size": 0
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    },
    {
        "items": [
            {
               "type": "FILE",
               "id": "8",
               "parentId": None,
               "size": 10
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]
IMPORT_BATCHES_8 = [
    {#- изменение типа элемента с папки на файл и с файла на папку не допускается
        "items": [
            {
                "type": "FOLDER",
                "id": "9",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    },
    {
        "items": [
            {
               "type": "FILE",
               "id": "9",
               "parentId": None,
               "size": 8
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]
IMPORT_BATCHES_9 = [
{
        "items": [
            {
                "type": "FOLDER",
                "id": "10",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-0"
    }
]

IMPORT_BATCHES_10 = [
{
        "items": [
            {
                "type": "FOLDER",
                "id": "11",
                "parentId": None
            },
            {
                "type": "FILE",
                "id": "12",
                "parentId": None
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    }
]


EXPECTED_TREE = {
    "type": "FOLDER",
    "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
    "size": 1984,
    "url": None,
    "parentId": None,
    "date": "2022-02-03T15:00:00Z",
    "children": [
        {
            "type": "FOLDER",
            "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
            "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            "size": 1600,
            "url": None,
            "date": "2022-02-03T15:00:00Z",
            "children": [
                {
                    "type": "FILE",
                    "url": "/file/url3",
                    "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
                    "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                    "size": 512,
                    "date": "2022-02-03T12:00:00Z",
                    "children": None,
                },
                {
                    "type": "FILE",
                    "url": "/file/url4",
                    "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                    "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                    "size": 1024,
                    "date": "2022-02-03T12:00:00Z",
                    "children": None
                },
                {
                    "type": "FILE",
                    "url": "/file/url5",
                    "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                    "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                    "size": 64,
                    "date": "2022-02-03T15:00:00Z",
                    "children": None
                }
            ]
        },
        {
            "type": "FOLDER",
            "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
            "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            "size": 384,
            "url": None,
            "date": "2022-02-02T12:00:00Z",
            "children": [
                {
                    "type": "FILE",
                    "url": "/file/url1",
                    "id": "863e1a7a-1304-42ae-943b-179184c077e3",
                    "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                    "size": 128,
                    "date": "2022-02-02T12:00:00Z",
                    "children": None
                },
                {
                    "type": "FILE",
                    "url": "/file/url2",
                    "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                    "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                    "size": 256,
                    "date": "2022-02-02T12:00:00Z",
                    "children": None
                }
            ]
        },
    ]
}


def request(path, method="GET", data=None, json_response=False):
    try:
        params = {
            "url": f"{API_BASEURL}{path}",
            "method": method,
            "headers": {},
        }

        if data:
            params["data"] = json.dumps(
                data, ensure_ascii=False).encode("utf-8")
            params["headers"]["Content-Length"] = len(params["data"])
            params["headers"]["Content-Type"] = "application/json"

        req = urllib.request.Request(**params)

        with urllib.request.urlopen(req) as res:
            res_data = res.read().decode("utf-8")
            if json_response:
                res_data = json.loads(res_data)
            return (res.getcode(), res_data)
    except urllib.error.HTTPError as e:
        return (e.getcode(), None)


def deep_sort_children(node):
    if node.get("children"):
        node["children"].sort(key=lambda x: x["id"])

        for child in node["children"]:
            deep_sort_children(child)


def print_diff(expected, response):
    with open("expected.json", "w") as f:
        json.dump(expected, f, indent=2, ensure_ascii=False, sort_keys=True)
        f.write("\n")

    with open("response.json", "w") as f:
        json.dump(response, f, indent=2, ensure_ascii=False, sort_keys=True)
        f.write("\n")

    subprocess.run(["git", "--no-pager", "diff", "--no-index",
                    "expected.json", "response.json"])


def test_import():
    for index, batch in enumerate(IMPORT_BATCHES):
        print(f"Importing batch {index}")
        status, _ = request("/imports", method="POST", data=batch)

        assert status == 200, f"Expected HTTP status code 200, got {status}"

    print("Test import passed.")


def test_nodes():
    status, response = request(f"/nodes/{ROOT_ID}", json_response=True)
    # print(json.dumps(response, indent=2, ensure_ascii=False))

    assert status == 200, f"Expected HTTP status code 200, got {status}"

    deep_sort_children(response)
    deep_sort_children(EXPECTED_TREE)
    if response != EXPECTED_TREE:
        print_diff(EXPECTED_TREE, response)
        print("Response tree doesn't match expected tree.")
        sys.exit(1)

    print("Test nodes passed.")


def test_updates():
    params = urllib.parse.urlencode({
        "date": "2022-02-04T00:00:00Z"
    })
    status, response = request(f"/updates?{params}", json_response=True)
    assert status == 200, f"Expected HTTP status code 200, got {status}"
    print("Test updates passed.")


def test_history():
    params = urllib.parse.urlencode({
        "dateStart": "2022-02-01T00:00:00Z",
        "dateEnd": "2022-02-03T00:00:00Z"
    })
    status, response = request(
        f"/node/{ROOT_ID}/history?{params}", json_response=True)
    assert status == 200, f"Expected HTTP status code 200, got {status}"
    print("Test stats passed.")


def test_delete():
    params = urllib.parse.urlencode({
        "date": "2022-02-04T00:00:00Z"
    })
    status, _ = request(f"/delete/{ROOT_ID}?{params}", method="DELETE")
    assert status == 200, f"Expected HTTP status code 200, got {status}"

    status, _ = request(f"/nodes/{ROOT_ID}", json_response=True)
    assert status == 404, f"Expected HTTP status code 404, got {status}"

    print("Test delete passed.")

def test_import_full():
    params = urllib.parse.urlencode({
        "date": "2022-02-04T00:00:00Z"
    })
    print("test  в одном запросе не может быть двух элементов с одинаковым id")
    for index, batch in enumerate(IMPORT_BATCHES_1):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
        if (index == 1):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 200, f"Expected HTTP status code 200, got {status}"
    print("Test passed: в одном запросе не может быть двух элементов с одинаковым id")

    print("test поле id не может быть равно null")
    for index, batch in enumerate(IMPORT_BATCHES_2):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
    print("Test passed: поле id не может быть равно null")
    print("test родителем элемента может быть только папка")
    for index, batch in enumerate(IMPORT_BATCHES_3):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
    print("Test passed: родителем элемента может быть только папка")

    print("test поле url при импорте папки всегда должно быть равно null")
    for index, batch in enumerate(IMPORT_BATCHES_4):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
        if (index == 1):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 200, f"Expected HTTP status code 200, got {status}"
    print("Test passed: поле url при импорте папки всегда должно быть равно null")


    for index, batch in enumerate(IMPORT_BATCHES_5):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
    print("Test passed: размер поля url при импорте файла всегда должен быть меньше либо равным 255")
    print("test поле size при импорте папки всегда должно быть равно null")
    for index, batch in enumerate(IMPORT_BATCHES_6):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
        if (index == 1):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 200, f"Expected HTTP status code 200, got {status}"
    print("Test passed: поле size при импорте папки всегда должно быть равно null")


    print("test поле size для файлов всегда должно быть больше 0")
    for index, batch in enumerate(IMPORT_BATCHES_7):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
        if (index == 1):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
        if (index == 2):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 200, f"Expected HTTP status code 200, got {status}"
    print("Test passed: поле size для файлов всегда должно быть больше 0")


    print("test изменение типа элемента с папки на файл и с файла на папку не допускается")
    for index, batch in enumerate(IMPORT_BATCHES_8):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 200, f"Expected HTTP status code 200, got {status}"
        if (index == 1):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
    print("Test passed: изменение типа элемента с папки на файл и с файла на папку не допускается")

    print("test дата в неправильном формате")
    for index, batch in enumerate(IMPORT_BATCHES_9):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"
        print(f"Getting node")
        status, _ = request("/node/10", method="GET")

        assert status == 404, f"Expected HTTP status code 404, got {status}"
    print("Test passed: дата в неправильном формате")



    for index, batch in enumerate(IMPORT_BATCHES_10):
        if (index == 0):
            print(f"Importing batch {index}")
            status, _ = request("/imports", method="POST", data=batch)

            assert status == 400, f"Expected HTTP status code 400, got {status}"

        print(f"Getting node")
        status, _ = request("/node/11", method="GET")
        assert status == 404, f"Expected HTTP status code 404, got {status}"
    print("Test passed: если 1 элем неправильный, то не добавляем")

    status, _ = request(f"/delete/1?{params}", method="DELETE")
    status, _ = request(f"/delete/2?{params}", method="DELETE")
    status, _ = request(f"/delete/3?{params}", method="DELETE")
    status, _ = request(f"/delete/4?{params}", method="DELETE")
    status, _ = request(f"/delete/5?{params}", method="DELETE")
    status, _ = request(f"/delete/6?{params}", method="DELETE")
    status, _ = request(f"/delete/7?{params}", method="DELETE")
    status, _ = request(f"/delete/8?{params}", method="DELETE")
    status, _ = request(f"/delete/9?{params}", method="DELETE")
    status, _ = request(f"/delete/10?{params}", method="DELETE")




def test_all():
    test_import()
    test_import_full()
#     test_nodes()
#     test_updates()
#     test_history()
    test_delete()





def main():
    global API_BASEURL
    test_name = None

    for arg in sys.argv[1:]:
        if re.match(r"^https?://", arg):
            API_BASEURL = arg
        elif test_name is None:
            test_name = arg

    if API_BASEURL.endswith('/'):
        API_BASEURL = API_BASEURL[:-1]

    print(f"Testing API on {API_BASEURL}")

    if test_name is None:
        test_all()
    else:
        test_func = globals().get(f"test_{test_name}")
        if not test_func:
            print(f"Unknown test: {test_name}")
            sys.exit(1)
        test_func()


if __name__ == "__main__":
    main()
