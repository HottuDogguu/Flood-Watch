package com.example.myapplication.ui.activity.users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.ImageView;

import com.example.myapplication.R;


public class MDRRMOContactsActivity extends AppCompatActivity {

    private Spinner locationSpinner;
    private TextView emergencyLocationText;
    private TextView emergencyHotlineNumber;
    private LinearLayout contactsContainer;
    private LinearLayout emergencyServicesContainer;
    private CardView emergencyBanner;

    private Map<String, LocationContacts> contactsMap;
    private String selectedLocation = "stacruz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdrrmo_contacts);

        initializeViews();
        initializeContactsData();
        setupLocationSpinner();
        updateContactsDisplay();
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        locationSpinner = findViewById(R.id.locationSpinner);
        emergencyLocationText = findViewById(R.id.emergencyLocationText);
        emergencyHotlineNumber = findViewById(R.id.emergencyHotlineNumber);
        contactsContainer = findViewById(R.id.contactsContainer);
        emergencyServicesContainer = findViewById(R.id.emergencyServicesContainer);
        emergencyBanner = findViewById(R.id.emergencyBanner);
    }

    private void setupLocationSpinner() {
        List<String> locations = new ArrayList<>();
        locations.add("Sta. Cruz");
        locations.add("Pagsanjan");
        locations.add("Pila");
        locations.add("Victoria");
        locations.add("Calauan");
        locations.add("Majayjay");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                locations
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: selectedLocation = "stacruz"; break;
                    case 1: selectedLocation = "pagsanjan"; break;
                    case 2: selectedLocation = "pila"; break;
                    case 3: selectedLocation = "victoria"; break;
                    case 4: selectedLocation = "calauan"; break;
                    case 5: selectedLocation = "majayjay"; break;
                }
                updateContactsDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initializeContactsData() {
        contactsMap = new HashMap<>();

        // Sta. Cruz
        LocationContacts staCruz = new LocationContacts("Sta. Cruz");
        staCruz.addOffice(new Office("MDRRMO Main Office", "Municipal Hall, Sta. Cruz",
                "+63 49 501 6234", "+63 917 123 4567", "mdrrmo.stacruz@laguna.gov.ph",
                "24/7 Emergency Hotline", "#EF4444"));
        staCruz.addOffice(new Office("Emergency Operations Center", "Municipal Hall, Sta. Cruz",
                "+63 49 501 8888", "+63 918 234 5678", "eoc.stacruz@laguna.gov.ph",
                "24/7 During Alerts", "#F97316"));
        staCruz.addOffice(new Office("Barangay Coordination Unit", "Various Barangays",
                "+63 49 501 7777", "+63 919 345 6789", "barangay.coord@stacruz.gov.ph",
                "Mon-Sat: 8AM-5PM", "#3B82F6"));
        staCruz.addOffice(new Office("Flood Monitoring Station", "Riverside Area, Sta. Cruz",
                "+63 49 501 9999", "+63 920 456 7890", "monitoring@mdrrmo-stacruz.gov.ph",
                "24/7 Monitoring", "#06B6D4"));
        staCruz.addEmergencyService(new EmergencyService("PNP Sta. Cruz", "+63 49 501 2345", "🚓"));
        staCruz.addEmergencyService(new EmergencyService("Fire Department", "+63 49 501 3456", "🚒"));
        staCruz.addEmergencyService(new EmergencyService("Medical Emergency", "+63 49 501 4567", "🚑"));
        contactsMap.put("stacruz", staCruz);

        // Pagsanjan
        LocationContacts pagsanjan = new LocationContacts("Pagsanjan");
        pagsanjan.addOffice(new Office("MDRRMO Main Office", "Municipal Hall, Pagsanjan",
                "+63 49 808 1234", "+63 917 234 5678", "mdrrmo.pagsanjan@laguna.gov.ph",
                "24/7 Emergency Hotline", "#EF4444"));
        pagsanjan.addOffice(new Office("Emergency Operations Center", "Municipal Hall, Pagsanjan",
                "+63 49 808 9999", "+63 918 345 6789", "eoc.pagsanjan@laguna.gov.ph",
                "24/7 During Alerts", "#F97316"));
        pagsanjan.addOffice(new Office("Barangay Coordination Unit", "Various Barangays",
                "+63 49 808 7777", "+63 919 456 7890", "barangay.coord@pagsanjan.gov.ph",
                "Mon-Sat: 8AM-5PM", "#3B82F6"));
        pagsanjan.addOffice(new Office("River Monitoring Station", "Pagsanjan Falls Area",
                "+63 49 808 8888", "+63 920 567 8901", "monitoring@mdrrmo-pagsanjan.gov.ph",
                "24/7 Monitoring", "#06B6D4"));
        pagsanjan.addEmergencyService(new EmergencyService("PNP Pagsanjan", "+63 49 808 2345", "🚓"));
        pagsanjan.addEmergencyService(new EmergencyService("Fire Department", "+63 49 808 3456", "🚒"));
        pagsanjan.addEmergencyService(new EmergencyService("Medical Emergency", "+63 49 808 4567", "🚑"));
        contactsMap.put("pagsanjan", pagsanjan);

        // Pila
        LocationContacts pila = new LocationContacts("Pila");
        pila.addOffice(new Office("MDRRMO Main Office", "Municipal Hall, Pila",
                "+63 49 557 1234", "+63 917 234 5678", "mdrrmo.pila@laguna.gov.ph",
                "24/7 Emergency Hotline", "#EF4444"));
        pila.addOffice(new Office("Emergency Operations Center", "Municipal Hall, Pila",
                "+63 49 557 9999", "+63 918 345 6789", "eoc.pila@laguna.gov.ph",
                "24/7 During Alerts", "#F97316"));
        pila.addOffice(new Office("Barangay Coordination Unit", "Various Barangays",
                "+63 49 557 7777", "+63 919 456 7890", "barangay.coord@pila.gov.ph",
                "Mon-Sat: 8AM-5PM", "#3B82F6"));
        pila.addOffice(new Office("Municipal Monitoring Station", "Pila Town Center",
                "+63 49 557 8888", "+63 920 567 8901", "monitoring@mdrrmo-pila.gov.ph",
                "24/7 Monitoring", "#06B6D4"));
        pila.addEmergencyService(new EmergencyService("PNP Pila", "+63 49 557 2345", "🚓"));
        pila.addEmergencyService(new EmergencyService("Fire Department", "+63 49 557 3456", "🚒"));
        pila.addEmergencyService(new EmergencyService("Medical Emergency", "+63 49 557 4567", "🚑"));
        contactsMap.put("pila", pila);

        // Victoria
        LocationContacts victoria = new LocationContacts("Victoria");
        victoria.addOffice(new Office("MDRRMO Main Office", "Municipal Hall, Victoria",
                "+63 49 559 1234", "+63 917 345 6789", "mdrrmo.victoria@laguna.gov.ph",
                "24/7 Emergency Hotline", "#EF4444"));
        victoria.addOffice(new Office("Emergency Operations Center", "Municipal Hall, Victoria",
                "+63 49 559 9999", "+63 918 456 7890", "eoc.victoria@laguna.gov.ph",
                "24/7 During Alerts", "#F97316"));
        victoria.addOffice(new Office("Barangay Coordination Office", "Various Barangays",
                "+63 49 559 7777", "+63 919 567 8901", "barangay.coord@victoria.gov.ph",
                "Mon-Sat: 8AM-5PM", "#3B82F6"));
        victoria.addOffice(new Office("Municipal Monitoring Center", "Victoria Town Proper",
                "+63 49 559 8888", "+63 920 678 9012", "monitoring@mdrrmo-victoria.gov.ph",
                "24/7 Monitoring", "#06B6D4"));
        victoria.addEmergencyService(new EmergencyService("PNP Victoria", "+63 49 559 2345", "🚓"));
        victoria.addEmergencyService(new EmergencyService("Fire Department", "+63 49 559 3456", "🚒"));
        victoria.addEmergencyService(new EmergencyService("Medical Emergency", "+63 49 559 4567", "🚑"));
        contactsMap.put("victoria", victoria);

        // Calauan
        LocationContacts calauan = new LocationContacts("Calauan");
        calauan.addOffice(new Office("MDRRMO Main Office", "Municipal Hall, Calauan",
                "+63 49 549 1234", "+63 917 456 7890", "mdrrmo.calauan@laguna.gov.ph",
                "24/7 Emergency Hotline", "#EF4444"));
        calauan.addOffice(new Office("Emergency Operations Center", "Municipal Hall, Calauan",
                "+63 49 549 9999", "+63 918 567 8901", "eoc.calauan@laguna.gov.ph",
                "24/7 During Alerts", "#F97316"));
        calauan.addOffice(new Office("Barangay Emergency Response", "Various Barangays",
                "+63 49 549 7777", "+63 919 678 9012", "barangay.coord@calauan.gov.ph",
                "Mon-Sat: 8AM-5PM", "#3B82F6"));
        calauan.addOffice(new Office("Flood Monitoring Unit", "Calauan Town Center",
                "+63 49 549 8888", "+63 920 789 0123", "monitoring@mdrrmo-calauan.gov.ph",
                "24/7 Monitoring", "#06B6D4"));
        calauan.addEmergencyService(new EmergencyService("PNP Calauan", "+63 49 549 2345", "🚓"));
        calauan.addEmergencyService(new EmergencyService("Fire Department", "+63 49 549 3456", "🚒"));
        calauan.addEmergencyService(new EmergencyService("Medical Emergency", "+63 49 549 4567", "🚑"));
        contactsMap.put("calauan", calauan);

        // Majayjay
        LocationContacts majayjay = new LocationContacts("Majayjay");
        majayjay.addOffice(new Office("MDRRMO Main Office", "Municipal Hall, Majayjay",
                "+63 49 558 1234", "+63 917 567 8901", "mdrrmo.majayjay@laguna.gov.ph",
                "24/7 Emergency Hotline", "#EF4444"));
        majayjay.addOffice(new Office("Emergency Operations Center", "Municipal Hall, Majayjay",
                "+63 49 558 9999", "+63 918 678 9012", "eoc.majayjay@laguna.gov.ph",
                "24/7 During Alerts", "#F97316"));
        majayjay.addOffice(new Office("Barangay Disaster Response", "Various Barangays",
                "+63 49 558 7777", "+63 919 789 0123", "barangay.coord@majayjay.gov.ph",
                "Mon-Sat: 8AM-5PM", "#3B82F6"));
        majayjay.addOffice(new Office("Municipal Monitoring Station", "Majayjay Town Proper",
                "+63 49 558 8888", "+63 920 890 1234", "monitoring@mdrrmo-majayjay.gov.ph",
                "24/7 Monitoring", "#06B6D4"));
        majayjay.addEmergencyService(new EmergencyService("PNP Majayjay", "+63 49 558 2345", "🚓"));
        majayjay.addEmergencyService(new EmergencyService("Fire Department", "+63 49 558 3456", "🚒"));
        majayjay.addEmergencyService(new EmergencyService("Medical Emergency", "+63 49 558 4567", "🚑"));
        contactsMap.put("majayjay", majayjay);
    }

    private void updateContactsDisplay() {
        LocationContacts contacts = contactsMap.get(selectedLocation);
        if (contacts == null) return;

        // Update emergency banner
        emergencyLocationText.setText("Emergency Hotline - " + contacts.locationName);
        if (!contacts.offices.isEmpty()) {
            emergencyHotlineNumber.setText(contacts.offices.get(1).phone); // Emergency Operations Center
        }

        emergencyBanner.setOnClickListener(v -> {
            if (!contacts.offices.isEmpty()) {
                dialPhone(contacts.offices.get(1).phone);
            }
        });

        // Clear and rebuild contacts
        contactsContainer.removeAllViews();
        for (Office office : contacts.offices) {
            addOfficeCard(office);
        }

        // Clear and rebuild emergency services
        emergencyServicesContainer.removeAllViews();
        for (EmergencyService service : contacts.emergencyServices) {
            addEmergencyServiceCard(service);
        }
    }

    private void addOfficeCard(Office office) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_office_contact, contactsContainer, false);

        TextView nameText = cardView.findViewById(R.id.officeName);
        TextView locationText = cardView.findViewById(R.id.officeLocation);
        TextView availabilityText = cardView.findViewById(R.id.officeAvailability);
        TextView phoneText = cardView.findViewById(R.id.officePhone);
        TextView mobileText = cardView.findViewById(R.id.officeMobile);
        TextView emailText = cardView.findViewById(R.id.officeEmail);
        View iconBackground = cardView.findViewById(R.id.iconBackground);

        nameText.setText(office.name);
        locationText.setText(office.location);
        availabilityText.setText(office.availability);
        phoneText.setText(office.phone);
        mobileText.setText(office.mobile);
        emailText.setText(office.email);

        phoneText.setOnClickListener(v -> dialPhone(office.phone));
        mobileText.setOnClickListener(v -> dialPhone(office.mobile));
        emailText.setOnClickListener(v -> sendEmail(office.email));

        contactsContainer.addView(cardView);
    }

    private void addEmergencyServiceCard(EmergencyService service) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_emergency_service, emergencyServicesContainer, false);

        TextView nameText = cardView.findViewById(R.id.serviceName);
        TextView numberText = cardView.findViewById(R.id.serviceNumber);
        TextView iconText = cardView.findViewById(R.id.serviceIcon);

        nameText.setText(service.name);
        numberText.setText(service.number);
        iconText.setText(service.icon);

        cardView.setOnClickListener(v -> dialPhone(service.number));

        emergencyServicesContainer.addView(cardView);
    }

    private void dialPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        startActivity(intent);
    }

    // Data classes
    static class LocationContacts {
        String locationName;
        List<Office> offices = new ArrayList<>();
        List<EmergencyService> emergencyServices = new ArrayList<>();

        LocationContacts(String locationName) {
            this.locationName = locationName;
        }

        void addOffice(Office office) {
            offices.add(office);
        }

        void addEmergencyService(EmergencyService service) {
            emergencyServices.add(service);
        }
    }

    static class Office {
        String name, location, phone, mobile, email, availability, color;

        Office(String name, String location, String phone, String mobile, String email, String availability, String color) {
            this.name = name;
            this.location = location;
            this.phone = phone;
            this.mobile = mobile;
            this.email = email;
            this.availability = availability;
            this.color = color;
        }
    }

    static class EmergencyService {
        String name, number, icon;

        EmergencyService(String name, String number, String icon) {
            this.name = name;
            this.number = number;
            this.icon = icon;
        }
    }
}