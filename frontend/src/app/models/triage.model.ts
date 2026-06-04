import { AttendanceRuleSummary } from './attendance-rule.model';

export interface TriageRequest {
  report: string;
}

export interface TriageResponse {
  specialty: string;
  urgency: string;
  summary: string;
  attendanceRules: AttendanceRuleSummary[];
}
