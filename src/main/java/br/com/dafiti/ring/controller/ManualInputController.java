/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.controller;

import br.com.dafiti.ring.model.ImportLog;
import br.com.dafiti.ring.model.ManualInput;
import br.com.dafiti.ring.model.DivisionGroup;
import br.com.dafiti.ring.model.Metadata;
import br.com.dafiti.ring.model.User;
import br.com.dafiti.ring.option.ImportLogStatus;
import br.com.dafiti.ring.service.ImportLogService;
import br.com.dafiti.ring.service.ManualInputService;
import br.com.dafiti.ring.service.MetadataService;
import br.com.dafiti.ring.service.RoleService;
import br.com.dafiti.ring.service.StorageManagerService;
import br.com.dafiti.ring.service.UserService;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author guilherme.almeida
 */
@Controller
@RequestMapping("/manual/input")
public class ManualInputController {

    private final MetadataService metadataService;
    private final ManualInputService manualInputService;
    private final StorageManagerService storageManagerService;
    private final ImportLogService importLogService;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public ManualInputController(MetadataService metadataService,
            ManualInputService manualInputService,
            StorageManagerService storageManagerService,
            ImportLogService importLogService,
            UserService userService,
            RoleService roleService) {
        this.metadataService = metadataService;
        this.manualInputService = manualInputService;
        this.storageManagerService = storageManagerService;
        this.importLogService = importLogService;
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * creates a new manualInput for configuration
     *
     * @param model
     * @param principal
     * @return
     */
    @GetMapping(path = "/create")
    public String newManualInput(Model model, Principal principal) {

        DivisionGroup divisionGroup = userService.findByUsername(principal.getName()).getDivisionGroup();

        model.addAttribute("manualInput", new ManualInput(divisionGroup));
        return "manualInput/edit";
    }

    /**
     * when the user clicks to remove some field from metadata, the process in
     * back-end is to disable the field instead of real delete from database so
     * we can keep fault tolerance when modifying a manualInput metadata
     *
     * @param model
     * @param manualInput
     * @param index
     * @return
     */
    @PostMapping(path = "/save", params = {"remove_field_from_metadata"})
    public String removeFieldFromMetadata(Model model,
            @ModelAttribute ManualInput manualInput,
            @RequestParam(value = "remove_field_from_metadata") int index) {

        manualInput.getMetadata().get(index).setIsActive(Boolean.FALSE);
        model.addAttribute("manualInput", manualInput);

        return "manualInput/edit";
    }

    /**
     * creates a new field, calculate the position based on manualInput object
     * target and add to metadata list of a manualInput
     *
     * @param model
     * @param manualInput
     * @return
     */
    @PostMapping(path = "/save", params = {"add_field_to_metadata"})
    public String addFieldToMetadata(Model model,
            @ModelAttribute ManualInput manualInput) {

        Integer nexPosition = manualInput.getMetadata().stream().mapToInt(m -> m.getOrdinalPosition()).max().getAsInt() + 1;
        manualInput.getMetadata().add(new Metadata(nexPosition));
        model.addAttribute("manualInput", manualInput);

        return "manualInput/edit";
    }

    /**
     * saves the manual input configurations and recreate the related collection
     * in mongoDB and table in MySql if exists
     *
     * @param model
     * @param manualInput
     * @return
     */
    @PostMapping(path = "/save", params = {"recreate_manual_input"})
    public String recreteAndSave(Model model,
            @ModelAttribute ManualInput manualInput) {

        List<Metadata> metadata = manualInput.getMetadata();
        metadataService.evaluateMetadata(metadata);

        model.addAttribute("manualInput", manualInput);
        boolean isMetadataValidated = metadataService.validate(metadata);

        if (!isMetadataValidated) {
            String errorMessage = metadataService.evaluateValidationMessagem(metadata);
            model.addAttribute("errorMessage", errorMessage);
            return "manualInput/edit";
        }

        manualInputService.save(manualInput);
        storageManagerService.createOrUpdateManualInput(manualInput, Boolean.TRUE);

        return "redirect:/manual/input/view/" + manualInput.getId();
    }

    /**
     * saves the manual input configurations and create the related collection
     * in mongoDB and table in MySql
     *
     * @param model
     * @param manualInput
     * @return
     */
    @PostMapping(path = "/save")
    public String save(Model model,
            @ModelAttribute ManualInput manualInput) {

        List<Metadata> metadata = manualInput.getMetadata();
        ManualInput lastPersist = manualInput.getId() == null ? null : manualInputService.findById(manualInput.getId());

        model.addAttribute("manualInput", manualInput);
        Boolean isMetadataValidated = metadataService.validate(metadata);
        Boolean isRecreate = lastPersist == null ? true : metadataService.hasDataTypeChange(metadata, lastPersist.getMetadata());

        if (!isMetadataValidated) {
            String errorMessage = metadataService.evaluateValidationMessagem(metadata);
            model.addAttribute("errorMessage", errorMessage);
            return "manualInput/edit";
        } else if (lastPersist != null && !manualInput.getName().equals(lastPersist.getName())) {
            model.addAttribute("errorMessage", "You can not change manual input name! :(");
            return "manualInput/edit";
        }

        storageManagerService.createOrUpdateManualInput(manualInput, isRecreate);

        manualInputService.save(manualInput);

        return "redirect:/manual/input/view/" + manualInput.getId();
    }

    /**
     * display the configuration of a ManualInput object and list its log only
     * available if the user own to the group of manual input
     *
     * @param model
     * @param principal
     * @param is_log_expanded
     * @param manualInput
     * @return
     */
    @GetMapping(path = "/view/{id}")
    public String viewManualConfiguration(Model model,
            Principal principal,
            @RequestParam(value = "log_expanded", required = false) boolean is_log_expanded,
            @PathVariable(value = "id") ManualInput manualInput) {

        User user = userService.findByUsername(principal.getName());
        if (!manualInput.getDivisionGroups().contains(user.getDivisionGroup())) {
            return "/403";
        }

        if (is_log_expanded) {
            model.addAttribute("showLog", true);
        }
        
        Pageable firstPageWithSixElemets = PageRequest.of(0, 6);
        List<ImportLog> importLogList = importLogService.findByManualInputOrderByCreatedAtDesc(manualInput, firstPageWithSixElemets);
        model.addAttribute("logList", importLogList);
        model.addAttribute("manualInput", manualInput);
        return "manualInput/view";
    }

    /**
     * open edit template to change manual input configurations only available
     * if the user own to the group of manual input
     *
     * @param model
     * @param principal
     * @param manualInput
     * @param redirectAttributes
     * @return
     */
    @GetMapping(path = "/edit/{id}")
    public String edit(Model model,
            Principal principal,
            RedirectAttributes redirectAttributes,
            @PathVariable(value = "id") ManualInput manualInput) {
        
        if(!hasPermission(principal, manualInput)) {
            redirectAttributes.addAttribute("error", "Sorry! You don't have permission to edit a Manul Input out of your group!");
            return "redirect:/manual/input/view/" + manualInput.getId();
        }

        User user = userService.findByUsername(principal.getName());
        if (!manualInput.getDivisionGroups().contains(user.getDivisionGroup())) {
            return "/403";
        }

        model.addAttribute("manualInput", manualInput);
        return "manualInput/edit";
    }
    
    /**
     *
     * @param model
     * @param principal
     * @param redirectAttributes
     * @param manualInput
     * @return
     */
    @GetMapping(path = "/delete/{id}")
    public String deleteManualInput(Model model,
            Principal principal,
            RedirectAttributes redirectAttributes,
            @PathVariable("id") ManualInput manualInput) {
        
        if(!hasPermission(principal, manualInput)) {
            redirectAttributes.addAttribute("error", "Sorry! You don't have permission to delete a Manul Input out of your group!");
            return "redirect:/manual/input/view/" + manualInput.getId();
        }
        
        storageManagerService.deleteManualInput(manualInput);
        manualInputService.delete(manualInput);
        redirectAttributes.addAttribute("success", "Manual Input was deleted!");
        return "redirect:/manual/input/list";
    }

    /**
     * list all manual inputs in group user is setted up if user has role LORD
     * than list all manual inputs of all groups
     *
     * @param model
     * @param principal
     * @return
     */
    @GetMapping(path = "/list")
    public String list(Model model, Principal principal) {
        
        model.addAttribute("manualInputList", manualInputService.findAll());

        /*User user = userService.findByUsername(principal.getName());

        if (user.getRoles().contains(roleService.findByName("LORD"))) {
            model.addAttribute("manualInputList", manualInputService.findAll());
        } else {
            DivisionGroup divisionGroup = userService.findByUsername(principal.getName()).getDivisionGroup();
            model.addAttribute("manualInputList", divisionGroup.getManualInput());
        }*/

        return "manualInput/list";
    }

    /**
     * receives and process a input file to insert in mongoDB and MySql
     *
     * @param multipartFile
     * @param manualInput
     * @param redirectAttributes
     * @param principal
     * @param model
     * @return
     */
    @PostMapping(path = "/uploadfile/{id}")
    public String submit(@RequestParam("upload_file") MultipartFile multipartFile,
            @PathVariable(value = "id") ManualInput manualInput,
            RedirectAttributes redirectAttributes,
            Principal principal,
            Model model) {
        
        if(!hasPermission(principal, manualInput)) {
            redirectAttributes.addAttribute("error", "Sorry! You don't have permission to upload a file here!");
            return "redirect:/manual/input/view/" + manualInput.getId();
        }

        ImportLog log = new ImportLog();
        log.setManualInput(manualInput);
        log.setCreatedBy(principal.getName());

        importLogService.updateLogText(log,
                log.getStatus(),
                false,
                "Reading file to the following metadata:\n" + manualInput.getInfo());

        InputStream file = null;
        try {

            file = multipartFile.getInputStream();

        } catch (IOException ex) {

            importLogService.updateLogText(log,
                    ImportLogStatus.ERROR,
                    true,
                    "ERROR: " + ex.toString());
            Logger.getLogger(ManualInputController.class.getName()).log(Level.SEVERE, null, ex);
            return "redirect:/manual/input/view/" + manualInput.getId() + "?log_expanded=true";

        }
        manualInputService.process(manualInput, file, log);

        return "redirect:/manual/input/view/" + manualInput.getId() + "?log_expanded=true";
    }
    
    /**
     *
     * @param principal
     * @param manualInput
     * @return
     */
    private boolean hasPermission(Principal principal, ManualInput manualInput) {

        User user = userService.findByUsername(principal.getName());

        if (user.getRoles().contains(roleService.findByName("LORD"))
                || manualInput.getDivisionGroups().contains(user.getDivisionGroup())) {
            return true;
        }

        return false;
    }
}
