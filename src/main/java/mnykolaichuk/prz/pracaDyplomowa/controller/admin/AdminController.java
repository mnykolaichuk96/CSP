package mnykolaichuk.prz.pracaDyplomowa.controller.admin;

import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteCarModelException;
import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteCityException;
import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteWorkshopWhileImplementationExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.City;
import mnykolaichuk.prz.pracaDyplomowa.service.CarModelService;
import mnykolaichuk.prz.pracaDyplomowa.service.CityService;
import mnykolaichuk.prz.pracaDyplomowa.service.CustomerService;
import mnykolaichuk.prz.pracaDyplomowa.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CarModelService carModelService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model
            , RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", model.getAttribute("errorMessage"));

        return "redirect:/showAllCustomer";
    }

    @GetMapping("/showAllCustomer")
    public String showAllCustomer(Model model) {

        model.addAttribute("customerDataList", customerService.getAllCustomerDataList());

        return "all-customer";
    }

    @PostMapping("/deleteCustomer")
    public ModelAndView deleteCustomer(
            @RequestParam("customerId") Integer customerId) {

        customerService.deleteByUsername(customerService.findById(customerId).getUsername());

        return new ModelAndView(new RedirectView("showAllCustomer"));
    }

    @GetMapping("/showAllWorkshop")
    public String showAllWorkshop(Model model) {

        model.addAttribute("workshopDataList", workshopService.getAllWorkshopDataList());

        return "admin/all-workshop";
    }

    @PostMapping("/deleteWorkshop")
    public ModelAndView deleteWorkshop(
            @RequestParam("workshopId") Integer workshopId) {

        try {
            workshopService.deleteByUsernameAndIsAdmin(workshopService.findById(workshopId).getUsername(),true);
        } catch (CantDeleteWorkshopWhileImplementationExistException e) {
            e.printStackTrace();
        }

        return new ModelAndView(new RedirectView("showAllWorkshop"));
    }

    @GetMapping("/showAllCity")
    public String showAllCity(Model model) {

        model.addAttribute("cityDataList", cityService.getAllCityDataList());

        return "admin/all-city";
    }

    @PostMapping("/deleteCity")
    public ModelAndView deleteCity(
            @RequestParam("cityId") Integer cityId
            , RedirectAttributes redirectAttributes) {

        try {
           cityService.deleteCityById(cityId);
        } catch (CantDeleteCityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("showAllCity"));
        }

        return new ModelAndView(new RedirectView("showAllCity"));
    }

    @PostMapping("/processAddCity")
    public ModelAndView deleteCity(
            @RequestParam("cityName") String cityName) {

        City city = new City(cityName);
        cityService.addCity(city);

        return new ModelAndView(new RedirectView("showAllCity"));
    }

    @GetMapping("/showAllCarModel")
    public String showAllCarModel(Model model) {

        model.addAttribute("carModelDataList", carModelService.getAllCarModelDataList());

        return "admin/all-car-model";
    }

    @PostMapping("/deleteCarModel")
    public ModelAndView deleteCarModel(
              @RequestParam("modelId") Integer modelId
            , RedirectAttributes redirectAttributes) {

        try {
            carModelService.deleteByCarModelId(modelId);
        } catch (CantDeleteCarModelException e) {
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("showAllCarModel"));
        }

        return new ModelAndView(new RedirectView("showAllCarModel"));
    }

    @PostMapping("/processAddCarModel")
    public ModelAndView deleteAddCarModel(
              @RequestParam("make") String make
            , @RequestParam("model") String model) {

       carModelService.addCarModel(make, model);
       return new ModelAndView(new RedirectView("showAllCarModel"));
    }
}
