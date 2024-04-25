package com.neotech.adminuser.controller;

import com.neotech.adminuser.dto.UserDto;
import com.neotech.adminuser.model.Data;
import com.neotech.adminuser.service.UserService;
import com.neotech.adminuser.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private DataService dataService;

    private List<String> months = Arrays.asList("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre");

    @GetMapping("/registration")
    public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
        return "register";
    }

    @PostMapping("/registration")
    public String saveUser(@ModelAttribute("user") UserDto userDto, Model model) {
        userDto.setRole("USER");
        userService.save(userDto);
        model.addAttribute("message", "Registered Successfuly!");
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("user-page")
    public String userPage (Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("user", userDetails);
        model.addAttribute("months", months);//aggiunto
        return "user";
    }
    // Mappa per associare il nome del mese al numero del mese
    private static final Map<String, Integer> monthMap = new HashMap<>();
    static {
        monthMap.put("Gennaio", 1);
        monthMap.put("Febbraio", 2);
        monthMap.put("Marzo", 3);
        monthMap.put("Aprile", 4);
        monthMap.put("Maggio", 5);
        monthMap.put("Giugno", 6);
        monthMap.put("Luglio", 7);
        monthMap.put("Agosto", 8);
        monthMap.put("Settembre", 9);
        monthMap.put("Ottobre", 10);
        monthMap.put("Novembre", 11);
        monthMap.put("Dicembre", 12);
    }

    @GetMapping("/select/{month}")
    public String showNumberSelection(@PathVariable String month, Principal principal, Model model) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("user", userDetails);
        model.addAttribute("selectedMonth", month);

        // Ottieni il numero di giorni per il mese selezionato
        int monthNumber = monthMap.get(month);
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), monthNumber);
        int daysInMonth = yearMonth.lengthOfMonth();

        List<Integer> numbers = new ArrayList<>();
        // Aggiungi tutti i giorni del mese alla lista
        for (int i = 1; i <= daysInMonth; i++) {
            numbers.add(i);
        }
        model.addAttribute("numbers", numbers);

        // Ottieni i nomi dei giorni della settimana
        List<String> daysOfWeek = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), i);
            daysOfWeek.add(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN));
        }
        model.addAttribute("daysOfWeek", daysOfWeek);
        return "number-selector";
    }

    @PostMapping("/save/{month}")
    public String saveSelection(@PathVariable String month, @RequestParam("selections") List<String> selections, @RequestParam("username") String username, Principal principal, Model model) { // Aggiunto il parametro per il nome utente) {
        // Ottieni il numero di giorni nel mese
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("user", userDetails);
        model.addAttribute("selectedMonth", month);
        int monthNumber = monthMap.get(month);
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), monthNumber);
        int numberOfDays = yearMonth.lengthOfMonth();

        // Itera su tutti i giorni del mese
        for (int i = 0; i < numberOfDays; i++) {
            LocalDate date = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), i + 1);
            String selection;
            // Determina se il giorno è festivo
            boolean isHoliday = isHoliday(yearMonth.atDay(i + 1));
            // Se il giorno è festivo o non è stato selezionato, imposta una selezione predefinita
            if (isHoliday || i >= selections.size()) {
                selection = "festivo";
            } else {
                selection = selections.get(i);
            }
            // Verifica se esistono già dati per la stessa data e lo stesso utente
            Data existingData = dataService.getDataByDateAndUsername(date, userDetails.getUsername());

            if (existingData != null) {
                // Sovrascrivi i dati esistenti con i nuovi
                existingData.setSelection(selection);
                dataService.saveData(existingData);
            } else {
                Data data = new Data();
                data.setUsername(userDetails.getUsername());
                data.setDate(date);
                data.setSelection(selection);
                dataService.saveData(data);
            }
        }
        return "sent-page";
    }

    // Metodo per calcolare se un giorno è festivo
    private boolean isHoliday(LocalDate date) {
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        int dayOfMonth = date.getDayOfMonth();
        if (month.equals("Aprile") && dayOfMonth == 25) {
            return true;
        }
        if (month.equals("Maggio") && dayOfMonth == 1) {
            return true;
        }
        if (month.equals("Agosto") && dayOfMonth == 15) {
            return true;
        }
        if (month.equals("Novembre") && dayOfMonth == 1) {
            return true;
        }
        if (month.equals("Dicembre") && dayOfMonth == 8) {
            return true;
        }
        if (month.equals("Dicembre") && dayOfMonth == 25) {
            return true;
        }
        if (month.equals("Dicembre") && dayOfMonth == 26) {
            return true;
        }
        if (month.equals("Gennaio") && dayOfMonth == 1) {
            return true;
        }
        if (month.equals("Gennaio") && dayOfMonth == 6) {
            return true;
        }

        return false;
    }

    @GetMapping("admin-page")
    public String adminPage (Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("user", userDetails);
        //Recupera tutti i dati dal database
        List<Data> dataList = dataService.getAllData();
        // lista di booleani per indicare se l'username è cambiato rispetto alla riga precedente
        List<Boolean> usernameChangedList = dataService.getUsernameChangedList(dataList);
        // Passa i dati alla Thymeleaf
        model.addAttribute("dataList", dataList);
        model.addAttribute("usernameChangedList", usernameChangedList);

        return "admin";
    }
}
