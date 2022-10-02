# GarbageOncall

Overengineered Discord bot hosted with AWS Lambda that reminds us to do the garbage on a weekly basis

## Set up
1. Go into `/lambda`
2. Rename `./com/ghoulean/garbageoncall/dagger/RotationConfigExample.java` to `com/ghoulean/garbageoncall/dagger/RotationConfig.java` and fill out the rotation config with your information
3. Build (`gradle build`)
4. Go into `/cdk`
5. Rename `./.env.example` to `./.env` and fill out the `DISCORD_TOKEN` and `DISCORD_CHANNEL_ID` to your Discord bot token and text channel ID, respectively
6. Bootstrap, synthesize, and deploy (`cdk bootstrap`, `cdk synth`, `cdk deploy`)
7. Manually insert the following entry into the deployed Dynamodb table (you only need to do this once):
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

## How to use
By default, every Wednesday at midnight-ish the Discord bot will spin up and send a message to a text channel about the current oncall and whether or not there is recycling. The cron job is configurable within cdk.

In order to forcefully update the next oncall information, go into the AWS account that the lambdas are deployed in and send the following payload to the `ChangeOncallLambda`:
```
{
  "personIndex": <int>,
  "recyclingIndex": <int>
}
```

You can send this payload by going into AWS Lambda -> select the ChangeOncallLambda -> "Test" tab
