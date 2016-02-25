from urllib2 import Request, urlopen

values = """
  {
    "login": "test",
    "password": "test"
  }
"""

headers = {
    'Content-Type': 'application/json'
}
request = Request('http://localhost:8080/api/user', data=values, headers=headers)

response_body = urlopen(request).read()
print response_body
