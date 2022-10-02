import { Effect, PolicyStatement } from "aws-cdk-lib/aws-iam";

export function getDynamoDbAccessPolicy(tableArn: string): PolicyStatement {
  return new PolicyStatement({
    actions: [
      "dynamodb:BatchGetItem",
      "dynamodb:GetItem",
      "dynamodb:Query",
      "dynamodb:Scan",
      "dynamodb:BatchWriteItem",
      "dynamodb:PutItem",
      "dynamodb:UpdateItem",
    ],
    effect: Effect.ALLOW,
    resources: [tableArn],
  });
}
