export interface ValidationPolicy {
  PolicyName: string;
  PolicyDescription: string;
}

export interface SignatureLevel {
  value: string;
  description: string;
}

export interface Certificate {
  id: string;
  qualifiedName: string;
}

export interface CertificateChain {
  Certificate: Certificate[];
}

export interface Error {
  value: string;
  Key: string;
}

export interface AdESValidationDetails {
  Error: Error[];
  Warning: any[];
  Info: any[];
}

export interface Error2 {
  value: string;
  Key: string;
}

export interface Warning {
  value: string;
  Key: string;
}

export interface QualificationDetails {
  Error: Error2[];
  Warning: Warning[];
  Info: any[];
}

export interface Signature {
  SigningTime: Date;
  BestSignatureTime: Date;
  SignedBy: string;
  SignatureLevel: SignatureLevel;
  SignatureScope?: any;
  Timestamps?: any;
  Filename?: any;
  CertificateChain: CertificateChain;
  Indication:
    | "FAILED"
    | "INDETERMINATE"
    | "NO_SIGNATURE_FOUND"
    | "PASSED"
    | "TOTAL_FAILED"
    | "TOTAL_PASSED";
  SubIndication: string;
  AdESValidationDetails: AdESValidationDetails;
  QualificationDetails: QualificationDetails;
  Id: string;
  CounterSignature?: any;
  ParentId?: any;
  SignatureFormat: string;
  ExtensionPeriodMin?: any;
  ExtensionPeriodMax?: any;
}

export interface SignatureOrTimestamp {
  Signature?: Signature;
  Timestamp?: any;
}

export interface ValidationReport {
  ValidationPolicy: ValidationPolicy;
  DocumentName: string;
  ValidSignaturesCount: number;
  SignaturesCount: number;
  ContainerType?: any;
  signatureOrTimestamp: SignatureOrTimestamp[];
  Semantic?: any;
  ValidationTime: Date;
}
