import { App } from "aws-cdk-lib";
import { GarbageOncallStack } from "./stacks/garbage-oncall-stack";

require("dotenv").config();

const app = new App();

const AWS_ENV_CONFIG = {};

new GarbageOncallStack(app, "GarbageOncallStack", {
  env: AWS_ENV_CONFIG,
});

app.synth();
