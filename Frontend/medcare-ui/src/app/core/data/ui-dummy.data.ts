/** UI-only sample data until APIs are wired. */

export const doctors = [
  { id: 'd1', name: 'Dr. Sarah Chen', specialty: 'General Medicine', fee: 85, rating: 4.8 },
  { id: 'd2', name: 'Dr. James Wilson', specialty: 'Cardiology', fee: 120, rating: 4.9 },
  { id: 'd3', name: 'Dr. Priya Patel', specialty: 'Pediatrics', fee: 75, rating: 4.7 },
];

export const availabilitySlots: Record<string, string[]> = {
  d1: ['09:00', '09:30', '10:00', '11:00', '14:00', '15:30'],
  d2: ['08:30', '10:30', '13:00', '16:00'],
  d3: ['09:00', '10:00', '11:30', '15:00'],
};

export const patientAppointments = [
  {
    id: 'a1',
    doctor: 'Dr. Sarah Chen',
    date: '2026-03-29',
    time: '10:00',
    status: 'confirmed',
    type: 'In clinic',
  },
  {
    id: 'a2',
    doctor: 'Dr. James Wilson',
    date: '2026-03-25',
    time: '14:00',
    status: 'completed',
    type: 'Video',
  },
];

export const prescriptions = [
  { id: 'rx1', drug: 'Amoxicillin 500mg', dosage: '1 cap × 3 daily', duration: '7 days', prescribed: '2026-03-20' },
  { id: 'rx2', drug: 'Paracetamol 500mg', dosage: '1–2 tabs PRN', duration: '5 days', prescribed: '2026-03-10' },
];

export const ehrRecords = [
  { id: 'e1', type: 'Diagnosis', summary: 'Acute bronchitis', date: '2026-03-20', provider: 'Dr. Chen' },
  { id: 'e2', type: 'Lab', summary: 'CBC — within normal limits', date: '2026-03-18', provider: 'Lab' },
];

export const labReports = [
  { id: 'r1', name: 'Complete Blood Count', date: '2026-03-18', result: 'Within range', status: 'final' },
  { id: 'r2', name: 'Lipid Panel', date: '2026-02-01', result: 'Borderline LDL', status: 'final' },
];

export const doctorPatients = [
  { id: 'p1', name: 'John Doe', age: 34, lastVisit: '2026-03-20', condition: 'Hypertension' },
  { id: 'p2', name: 'Emma Stone', age: 28, lastVisit: '2026-03-15', condition: 'Asthma follow-up' },
  { id: 'p3', name: 'Michael Brown', age: 52, lastVisit: '2026-03-10', condition: 'Diabetes Type 2' },
];

export const doctorTodayAppointments = [
  { id: 't1', patient: 'John Doe', time: '09:00', status: 'pending' as 'pending' | 'accepted' },
  { id: 't2', patient: 'Emma Stone', time: '10:30', status: 'accepted' },
  { id: 't3', patient: 'Alex Kim', time: '11:15', status: 'pending' },
];

export const earningsByMonth = [
  { month: 'Nov', amount: 12400 },
  { month: 'Dec', amount: 13200 },
  { month: 'Jan', amount: 14100 },
  { month: 'Feb', amount: 13800 },
  { month: 'Mar', amount: 15200 },
];

export const patientsDirectory = [
  { id: 'pt1', name: 'John Doe', age: 34, gender: 'M', phone: '+1 555-0101', address: '12 Oak St' },
  { id: 'pt2', name: 'Emma Stone', age: 28, gender: 'F', phone: '+1 555-0102', address: '44 Pine Ave' },
];

export const receptionistStats = {
  totalPatients: 842,
  todayAppointments: 23,
  availableDoctors: 6,
  revenueToday: 4250,
};

export const billingItems = [
  { id: 'b1', patient: 'John Doe', amount: 85, method: 'Cash' as const, date: '2026-03-29' },
  { id: 'b2', patient: 'Emma Stone', amount: 120, method: 'Online' as const, date: '2026-03-28' },
];
