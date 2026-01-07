package com.example.hospitalmanagement.api;

import com.example.hospitalmanagement.model.*;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    // ==================== AUTHENTICATION ====================
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // ==================== USERS ====================
    @POST("users")
    Call<UserResponse> createUser(@Body UserCreate userCreate);

    @GET("users")
    Call<List<UserResponse>> getAllUsers();

    // ==================== DOCTORS ====================
    @POST("doctors")
    Call<DoctorResponse> createDoctor(@Body DoctorCreate doctorCreate);

    @GET("doctors")
    Call<List<DoctorResponse>> getAllDoctors();

    @GET("doctors/{doctor_id}")
    Call<DoctorResponse> getDoctorById(@Path("doctor_id") int doctorId);

    // ==================== PATIENTS ====================
    @POST("patients")
    Call<PatientResponse> createPatient(@Body PatientCreate patientCreate);

    @GET("patients")
    Call<List<PatientResponse>> getAllPatients();

    @GET("patients/{patient_id}")
    Call<PatientResponse> getPatientById(@Path("patient_id") int patientId);

    // ==================== SPECIALIZATIONS ====================
    @POST("specializations")
    Call<SpecializationResponse> createSpecialization(@Body SpecializationCreate specializationCreate);

    @GET("specializations")
    Call<List<SpecializationResponse>> getAllSpecializations();

    @POST("doctor-specializations")
    Call<MessageResponse> addDoctorSpecialization(@Body DoctorSpecializationCreate doctorSpecializationCreate);

    // ==================== DOCTOR AVAILABILITY ====================
    @POST("availability")
    Call<DoctorAvailabilityResponse> createAvailability(@Body DoctorAvailabilityCreate availabilityCreate);

    @GET("availability/doctor/{doctor_id}")
    Call<List<DoctorAvailabilityResponse>> getDoctorAvailability(@Path("doctor_id") int doctorId);

    // ==================== APPOINTMENT REQUESTS ====================
    @POST("appointment-requests")
    Call<AppointmentRequestResponse> createAppointmentRequest(@Body AppointmentRequestCreate appointmentRequestCreate);

    @GET("appointment-requests")
    Call<List<AppointmentRequestResponse>> getAllAppointmentRequests(@Query("status") String status);

    @GET("appointment-requests/patient/{patient_id}")
    Call<List<AppointmentRequestResponse>> getPatientAppointmentRequests(@Path("patient_id") int patientId);

    @PUT("appointment-requests/{request_id}")
    Call<MessageResponse> updateAppointmentRequest(
            @Path("request_id") int requestId,
            @Body AppointmentRequestUpdate appointmentRequestUpdate);

    // ==================== APPOINTMENTS ====================
    @POST("appointments")
    Call<AppointmentResponse> createAppointment(@Body AppointmentCreate appointmentCreate);

    @GET("appointments")
    Call<List<AppointmentResponse>> getAllAppointments(
            @Query("doctor_id") Integer doctorId,
            @Query("patient_id") Integer patientId,
            @Query("status") String status);

    @GET("appointments/{appointment_id}")
    Call<AppointmentResponse> getAppointmentById(@Path("appointment_id") int appointmentId);

    @PUT("appointments/{appointment_id}")
    Call<MessageResponse> updateAppointment(
            @Path("appointment_id") int appointmentId,
            @Body AppointmentUpdate appointmentUpdate);

    // Helper methods for common appointment queries
    @GET("appointments")
    Call<List<AppointmentResponse>> getAppointmentsByDoctor(@Query("doctor_id") int doctorId);

    @GET("appointments")
    Call<List<AppointmentResponse>> getAppointmentsByPatient(@Query("patient_id") int patientId);

    // ==================== CHATS ====================
    @POST("chats")
    Call<ChatResponse> sendMessage(@Body ChatCreate chatCreate);

    @GET("chats/conversation/{user1_id}/{user2_id}")
    Call<List<ChatResponse>> getConversation(
            @Path("user1_id") int user1Id,
            @Path("user2_id") int user2Id);

    @GET("chats/user/{user_id}")
    Call<List<ChatResponse>> getUserChats(@Path("user_id") int userId);

    // ==================== PRESCRIPTIONS ====================
    @POST("prescriptions")
    Call<PrescriptionResponse> createPrescription(@Body PrescriptionCreate prescriptionCreate);

    @GET("prescriptions/patient/{patient_id}")
    Call<List<PrescriptionResponse>> getPatientPrescriptions(@Path("patient_id") int patientId);

    @POST("prescription-items")
    Call<PrescriptionItemResponse> addPrescriptionItem(@Body PrescriptionItemCreate prescriptionItemCreate);

    // ==================== DASHBOARD & STATISTICS ====================
    @GET("stats/dashboard")
    Call<DashboardStats> getDashboardStats();
}
