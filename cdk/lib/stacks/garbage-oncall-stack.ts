import path = require("path");
import dotenv = require("dotenv");

import { Duration, Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { AttributeType, BillingMode, Table } from "aws-cdk-lib/aws-dynamodb";
import { Rule, Schedule } from "aws-cdk-lib/aws-events";
import { LambdaFunction } from "aws-cdk-lib/aws-events-targets";
import { Code, Function, Runtime } from "aws-cdk-lib/aws-lambda";
import { getDynamoDbAccessPolicy } from "../helper/iam";

const JAR_FILE_LOCATION = "../../../lambda/build/distributions/GarbageOncall-0.1.0.zip";

export class GarbageOncallStack extends Stack {
  public readonly table: Table;
  public readonly oncallUpdate: Function;
  public readonly changeOncall: Function;
  public readonly oncallUpdateRule: Rule;

  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    this.table = new Table(this, "NextOncallTable", {
      partitionKey: {
        name: "id",
        type: AttributeType.NUMBER,
      },
      billingMode: BillingMode.PAY_PER_REQUEST,
    });

    const DEFAULT_LAMBDA_PROPS = {
      environment: {
        TABLE_NAME: this.table.tableName,
        DISCORD_TOKEN: process.env.DISCORD_TOKEN!,
        DISCORD_CHANNEL_ID: process.env.DISCORD_CHANNEL_ID!,
      },
      memorySize: 512,
      runtime: Runtime.JAVA_11,
      timeout: Duration.minutes(1),
    };

    this.oncallUpdate = new Function(this, "OncallUpdateLambda", {
      code: Code.fromAsset(path.resolve(__dirname, JAR_FILE_LOCATION)),
      ...DEFAULT_LAMBDA_PROPS,
      handler: "com.ghoulean.garbageoncall.lambda.OncallUpdateLambda",
    });

    this.changeOncall = new Function(this, "ChangeOncallLambda", {
      code: Code.fromAsset(path.resolve(__dirname, JAR_FILE_LOCATION)),
      ...DEFAULT_LAMBDA_PROPS,
      handler: "com.ghoulean.garbageoncall.lambda.ChangeOncallLambda",
    });

    this.oncallUpdate.addToRolePolicy(
      getDynamoDbAccessPolicy(this.table.tableArn)
    );
    this.changeOncall.addToRolePolicy(
      getDynamoDbAccessPolicy(this.table.tableArn)
    );

    this.oncallUpdateRule = new Rule(this, "TriggerOncallUpdateSchedule", {
      description: "Run OncallUpdateLambda once a week every Wednesday",
      enabled: true,
      ruleName: "TriggerOncallUpdateSchedule",
      schedule: Schedule.cron({
        weekDay: "WED",
        hour: "3",
        minute: "0",
      }),
      targets: [new LambdaFunction(this.oncallUpdate, {})],
    });
  }
}
