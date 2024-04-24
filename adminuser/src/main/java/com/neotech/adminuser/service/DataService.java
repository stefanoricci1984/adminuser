package com.neotech.adminuser.service;

import com.neotech.adminuser.model.Data;
import com.neotech.adminuser.repositories.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private DataRepository dataRepository;

    public List<Data> getAllData() {
        return dataRepository.findAll();
    }
    public void saveData(Data data) {dataRepository.save(data);
    }

    public Data getDataByDateAndUsername(LocalDate date, String username) {
        return dataRepository.findByDateAndUsername(date, username);
    }

    public List<Boolean> getUsernameChangedList(List<Data> dataList) {
        List<Boolean> usernameChangedList = new ArrayList<>();
        // Inizializza l'username del record precedente al primo username nel dataset
        String previousUsername = dataList.isEmpty() ? null : dataList.get(0).getUsername();
        LocalDate previousDate = dataList.isEmpty() ? null : dataList.get(0).getDate();
        // Inizializza il primo valore di usernameChangedList a true per mostrare il primo username
        usernameChangedList.add(true);
        // Itera attraverso i record e controlla se l'username è cambiato rispetto al record precedente
        for (int i = 1; i < dataList.size(); i++) { // Parti da 1 per saltare il primo record
            String currentUsername = dataList.get(i).getUsername();
            LocalDate currentDate = dataList.get(i).getDate();
            boolean usernameChanged = !currentUsername.equals(previousUsername);
            if (!usernameChanged) {
                if (currentDate.getMonth() != previousDate.getMonth()) {
                    usernameChanged = true; // Cambia lo username se il mese è diverso
                }
            }
            usernameChangedList.add(usernameChanged);
            previousUsername = currentUsername;
            previousDate = currentDate;
        }
        return usernameChangedList;
    }
}
