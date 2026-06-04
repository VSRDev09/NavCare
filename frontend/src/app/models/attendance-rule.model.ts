export interface AttendanceRule {
  id: number;
  averageWaitTime: number;
  acceptsEmergency: boolean;
  notes: string;
  specialtyName: string;
}

export interface AttendanceRuleRequest {
  averageWaitTime: number;
  acceptsEmergency: boolean;
  notes: string;
  specialtyId: number | null;
}

export interface AttendanceRuleSummary {
  averageWaitTime: number;
  acceptsEmergency: boolean;
  notes: string;
}
