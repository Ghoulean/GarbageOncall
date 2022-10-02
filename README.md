The following entry must be manually inserted into the created Dynamodb table if it does not exist:

```
{
  "id": {
    "N": "0"
  },
  "indices": {
    "L": [
      {
        "N": "0"
      },
      {
        "N": "0"
      }
    ]
  }
}
```
