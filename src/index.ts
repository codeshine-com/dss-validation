import { executeJar } from "node-java-connector";
import path from "path";

import { ValidationReport } from "./interfaces";

const JAR_PATH = path.resolve(__dirname, "./jar/dss-validation-cli.jar");

function getResultFromJar(jarPath: string, args: string[]): Promise<string> {
  return new Promise(async (resolve, reject) => {
    const process = await executeJar(jarPath, args);
    const chunks: any[] = [];

    process.stdout?.on("data", (chunk) => chunks.push(Buffer.from(chunk)));
    process.stderr?.on("data", (error) => reject(error));
    process.stdout?.on("end", () =>
      resolve(Buffer.concat(chunks).toString("utf8"))
    );
  });
}

export async function validateDocument(
  documentPath: string
): Promise<ValidationReport> {
  const jsonStringResult = await getResultFromJar(JAR_PATH, [documentPath]);

  return JSON.parse(jsonStringResult) as ValidationReport;
}
