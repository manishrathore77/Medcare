import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-patient-appointments-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './patient-appointments-shell.html',
  styleUrl: './patient-appointments-shell.scss',
})
export class PatientAppointmentsShell {}
