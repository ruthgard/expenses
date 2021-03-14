package org.ruthgard.expenses;

import org.ruthgard.expenses.model.Expense;
import org.ruthgard.expenses.model.Settings;
import org.ruthgard.expenses.model.User;
import org.ruthgard.expenses.model.Wallet;
import org.ruthgard.expenses.repo.ExpenseRepository;
import org.ruthgard.expenses.repo.SettingsRepository;
import org.ruthgard.expenses.repo.UserRepository;
import org.ruthgard.expenses.repo.WalletRepository;
import org.ruthgard.expenses.util.WalletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @GetMapping("/")
    public String homePage(Model model, HttpServletRequest request) {
        prepareHeader(request);
        return "redirect:expense";
    }
    @GetMapping("/expense")
    public String expensePage(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size")Optional<Integer> size, HttpServletRequest request) {
        prepareHeader(request);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(30);
        prepareExpense(model,null, currentPage,pageSize);
        HashMap<Wallet, Double> standings = new HashMap<>();
        for(Wallet w : walletRepository.findAll()) {
            standings.put(w, getWalletBalance(w.getId()));
        }
        model.addAttribute("settings", settingsRepository.findAll().iterator().next());
        model.addAttribute("standings", standings);
        return "expense";
    }
    @GetMapping("/standings")
    public String standingsPage(Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size")Optional<Integer> size, HttpServletRequest request) {
        prepareHeader(request);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(30);
        prepareExpense(model,null, currentPage,pageSize);
        HashMap<Wallet, Double> standings = new HashMap<>();
        for(Wallet w : walletRepository.findAll()) {
            standings.put(w, getWalletBalance(w.getId()));
        }
        model.addAttribute("settings", settingsRepository.findAll().iterator().next());
        model.addAttribute("standings", standings);
        return "standings";
    }
    @GetMapping("/settings")
    public String settingsPage(Model model, HttpServletRequest request) {
        prepareHeader(request);
        User u = new User();
        Wallet w = new Wallet();
        model.addAttribute("user", u);
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("wallet", w);
        model.addAttribute("allWallets", walletRepository.findAll());
        return "settings";
    }
    @GetMapping("/user")
    public String userPage(Model model, HttpServletRequest request) {
        prepareHeader(request);
        User u = new User();
        model.addAttribute("user", u);
        return "user";
    }
    @PostMapping("/addUser")
    public String addUserAction(@ModelAttribute("user") User u, BindingResult bindingResult ) {
        if( bindingResult.getErrorCount() == 0 ) {
            if(userRepository.findById(u.getId()).isPresent()) {
                User uInDB = userRepository.findById(u.getId()).get();
                uInDB.setEmail(u.getEmail());
                uInDB.setName(u.getName());
                uInDB.setAdmin(u.isAdmin());
                if( u.getPassword() != null && ! u.getPassword().isBlank()) {
                    uInDB.setPasswordButDontEncode( u.getPassword());
                }
                userRepository.save(uInDB);
            } else {
                u = userRepository.save(u);
            }
        }
        return "redirect:/settings";
    }
    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        prepareHeader(request);
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Id:" + id));
        model.addAttribute("user", u);
        return "user";
    }
    @PostMapping("/updateSettings")
    public String updateSettings(@ModelAttribute("settings") Settings s, Model model, BindingResult bindingResult ) {
        if( bindingResult.getErrorCount() == 0 ) {
            if (settingsRepository.findById(s.getId()).isPresent()) {
                Settings settingsInDB = settingsRepository.findById(s.getId()).get();
                settingsInDB.setDate(s.getDate());
                settingsRepository.save(settingsInDB);
            }
        }
        return "redirect:standings";
    }
    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Id:" + id));
        userRepository.delete(u);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }
    @GetMapping("/wallet")
    public String walletPage(Model model, HttpServletRequest request) {
        prepareHeader(request);
        Wallet w = new Wallet();
        ArrayList<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        for (Wallet wlist : walletRepository.findAll()) {
            userList.removeAll(wlist.getUsers());
        }
        model.addAttribute("wallet", w);
        model.addAttribute("allUsers", userList);
        return "wallet";
    }
    @PostMapping("/addWallet")
    public String addWalletAction(@ModelAttribute("wallet") Wallet w, @RequestParam(value="UserInWallet", required = false) int[] users, BindingResult bindingResult ) {
        if( bindingResult.getErrorCount() == 0 ) {
            if(walletRepository.findById(w.getId()).isPresent()) {
                Wallet uInDB = walletRepository.findById(w.getId()).get();
                uInDB.setName(w.getName());
                uInDB.setIcon(w.getIcon());
                uInDB.getUsers().clear();
                if( users != null)
                for(long i : users) {
                    uInDB.getUsers().add(userRepository.findById(i).orElseThrow(()-> new IllegalArgumentException("Invalid user Id:" + i)));
                }
                walletRepository.save(uInDB);
            } else {
                if( users != null)
                for(long i : users) {
                    w.getUsers().add(userRepository.findById(i).orElseThrow(()-> new IllegalArgumentException("Invalid user Id:" + i)));
                }
                w = walletRepository.save(w);
            }
        }
        return "redirect:/settings";
    }
    @GetMapping("/editWallet/{id}")
    public String editWallet(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        prepareHeader(request);
        Wallet w = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Walet Id:" + id));

        ArrayList<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        for (Wallet wlist : walletRepository.findAll()) {
            userList.removeAll(wlist.getUsers());
        }
        userList.addAll(w.getUsers());
        model.addAttribute("allUsers", userList);
        model.addAttribute("wallet", w);
        return "wallet";
    }
    @GetMapping("/deleteWallet/{id}")
    public String deleteWallet(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        Wallet w = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Wallet Id:" + id));
        walletRepository.delete(w);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }
    @RequestMapping(value = "/registerExpense", method = RequestMethod.POST)
    public String expensePage(@ModelAttribute("exp") Expense e, @RequestParam(value="payees", required = false) int[] payees, @RequestParam(value="buyees", required = false) int[] buyees, BindingResult bindingResult, Model model){
        if( bindingResult.getErrorCount() == 0 ) {
            if(expenseRepository.findById(e.getId()).isPresent()) {
                // Current ID found, fetch from database and update.
                Expense eInDB = expenseRepository.findById(e.getId()).get();
                eInDB.setName(e.getName());
                eInDB.setDescription(e.getDescription());
                eInDB.setDate(e.getDate());
                eInDB.setAmount(e.getAmount());
                addWalletsToExpense(payees,eInDB.getPayers());
                addWalletsToExpense(buyees,eInDB.getByers());

                e = expenseRepository.save(eInDB);
            } else {
                // The id was not found, creating new post.
                addWalletsToExpense(payees,e.getPayers());
                addWalletsToExpense(buyees,e.getByers());
               e = expenseRepository.save(e);
            }
            return "redirect:/expense";
        }
        return "redirect:/expense";
    }
    @GetMapping("/editExpense/{id}")
    public String editExpense(@PathVariable("id") long id, Model model, @RequestParam("page")Optional<Integer> page, @RequestParam("size")Optional<Integer> size, HttpServletRequest request) {
        prepareHeader(request);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(30);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid expense Id:" + id));
        prepareExpense(model,expense,currentPage,pageSize);
        HashMap<Wallet, Double> standings = new HashMap<>();
        for(Wallet w : walletRepository.findAll()) {
            standings.put(w, getWalletBalance(w.getId()));
        }
        model.addAttribute("settings", settingsRepository.findAll().iterator().next());
        model.addAttribute("standings", standings);
        return "expense";
    }

    @GetMapping("/deleteExpense/{id}")
    public String deleteExpense(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Expense Id:" + id));
        expenseRepository.delete(expense);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }

    private void prepareExpense(Model model, Expense expense, int currentPage, int pageSize) {
        if (expense == null) {
            expense = new Expense();
            expense.setDate(new Date());
            expense.setAmount(null);
            ArrayList<Wallet> wallets = new ArrayList<>();
            for( Wallet w  : walletRepository.findAll()) {
                wallets.add(w);
                if( w.getUsers().contains(getLoggedinUser())) {
                    expense.getPayers().add(w);
                }
            }
            expense.setByers(wallets);
        }

        int  numPages = (  (int)expenseRepository.count(settingsRepository.findAll().iterator().next().getDate()) / pageSize) +1;
        if(numPages > 0 ) {
            List<Integer> pageNumbers = getPageRange(currentPage, numPages);
            model.addAttribute("pageNumbers", pageNumbers);
        }

        WalletUtil payeesUtil = new WalletUtil(expense.getPayers());
        WalletUtil buyersUtil = new WalletUtil(expense.getByers());


        model.addAttribute("exp", expense);
        model.addAttribute( "payees", payeesUtil);
        model.addAttribute( "buyers", buyersUtil);
        model.addAttribute("numPages", numPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("allWallets", walletRepository.findAll());
        model.addAttribute("expenses", expenseRepository.findBeforeDate( settingsRepository.findAll().iterator().next().getDate(),
                PageRequest.of(currentPage -1, pageSize) ) );
    }

    private List<Integer> getPageRange(int currentPage, int numPages) {
        int startNumber = currentPage - 4 > 0 ? currentPage -2 : 1;
        int endNumber = numPages - startNumber > 3 ? startNumber +5 : numPages;
        if (endNumber > numPages)  endNumber = numPages;
        if(endNumber - startNumber < 6 && numPages > 5)  startNumber = endNumber -5;
        return IntStream.rangeClosed(startNumber, endNumber).boxed().collect(Collectors.toList());
    }

    private List<Wallet> addWalletsToExpense(int[] walletIds, List<Wallet> wallets) {
        wallets.clear();
        for( int wallet : walletIds) {
            if( walletRepository.findById((long)wallet).isPresent() ) {
                if( wallets == null )
                    wallets =  new ArrayList<Wallet>();
                if( ! wallets.contains(walletRepository.findById((long) wallet)))
                    wallets.add(walletRepository.findById((long) wallet).get());
            }
        }
        return wallets;
    }
    private List<Long> getIdArrayFromWallets(List<Wallet> wallets) {
        ArrayList<Long> idList = new ArrayList<>();
        for( Wallet wallet : wallets) {
            idList.add(wallet.getId());
        }
        return idList;
    }
    private Double getWalletBalance(long walletID) {
        Wallet thisWallet = walletRepository.findById(walletID).orElseThrow(() -> new IllegalArgumentException("No wallet found with id " + walletID));
        long myWeight = thisWallet.getSize();
        List<Expense> allExpenses = expenseRepository.findBeforeDate(settingsRepository.findAll().iterator().next().getDate());

        List<Expense> payerExpenses = new ArrayList<>();
        List<Expense> buyerExpenses = new ArrayList<>();

        for (Expense expense : allExpenses) {
            if (expense.getPayers().contains(thisWallet)) {
                payerExpenses.add(expense);
            }
            if (expense.getByers().contains(thisWallet)) {
                buyerExpenses.add(expense);
            }
        }

        Double paid = Double.valueOf(0);
        Double baught = Double.valueOf(0);

        for (Expense b : buyerExpenses) {
            Double amount = b.getAmount();
            int size = 0;
            for (Wallet buyer : b.getByers()) {
                size += buyer.getSize();
            }
            baught += ( amount /  size ) * myWeight;
        }
        for (Expense p : payerExpenses) {
            Double amount = p.getAmount();
            int size = 0;
            for (Wallet payer : p.getPayers()) {
                size += payer.getSize();
            }
            paid += ( amount /  size ) * myWeight;
        }
        return paid - baught;
    }

    private void prepareHeader(HttpServletRequest request) {
        User loggedInUser = getLoggedinUser();
        Wallet wallet = null;
        Double balance = null;
            if( loggedInUser == null)
                return;
            for( Wallet actualWallet : walletRepository.findAll()) {
                if ( actualWallet.getUsers().contains(loggedInUser)) {
                    wallet = actualWallet;
                    balance = getWalletBalance(actualWallet.getId());
                }
            }
            request.getSession().setAttribute("wallet", wallet);
            request.getSession().setAttribute("balance", balance);
        }

    private User getLoggedinUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();

            List<User> loggedInUsers = userRepository.findByName(username);
            User loggedInUser = null;
            if (loggedInUsers.size() == 1)
                return loggedInUsers.get(0);
            else
                return null;
        }
        return null;
    }
}
