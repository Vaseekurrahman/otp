package com.otpapp.otp.controller;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.otpapp.otp.api.SmsSender;
import com.otpapp.otp.dto.AdminInfoDto;
import com.otpapp.otp.dto.EnquiryDto;
import com.otpapp.otp.dto.StudentInfoDto;
import com.otpapp.otp.model.AdminInfo;
import com.otpapp.otp.model.Enquiry;
import com.otpapp.otp.model.StudentInfo;
import com.otpapp.otp.service.AdminInfoRepo;
import com.otpapp.otp.service.EnquiryRepo;
import com.otpapp.otp.service.StudentInfoRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	@Autowired
	EnquiryRepo erepo;
	@Autowired
	StudentInfoRepo sirepo;
	@Autowired
	AdminInfoRepo adminrepo;

	@GetMapping("/home")
	public String showIndex() {
		return "index";
	}

	@GetMapping("/apply")
	public String showApply(Model model) {
		StudentInfoDto dto = new StudentInfoDto();
		model.addAttribute("dto", dto);
		return "apply";
	}

	@GetMapping("/contact")
	public String showContact(Model model) {
		EnquiryDto dto = new EnquiryDto();
		model.addAttribute("dto", dto);
		return "contact";
	}

	@GetMapping("/expert")
	public String showExperts() {
		return "expert";
	}

	@GetMapping("/login")
	public String showLogin(Model model) {
		StudentInfoDto dto=new StudentInfoDto();
		model.addAttribute("dto", dto);
		return "login";
	}
	
	
	@GetMapping("/adminLogin")
	public String showAdminLogin(Model model) {
		AdminInfoDto dto=new AdminInfoDto();
		model.addAttribute("dto", dto);
		return "adminLogin";
	}
	
	
	@GetMapping("/forget")
	public String showForget() {
		return "forget";
	}
	
	@PostMapping("/contact")
	public String submitEnquiry(@ModelAttribute EnquiryDto enquiryDto, BindingResult result,
			RedirectAttributes redirectAttributes) {
		try {
			Enquiry eq = new Enquiry();
			eq.setName(enquiryDto.getName());
			eq.setGender(enquiryDto.getGender());
			eq.setContactno(enquiryDto.getContactno());
			eq.setEmailaddress(enquiryDto.getEmailaddress());
			eq.setEnquirytext(enquiryDto.getEnquirytext());
			eq.setPosteddate(new Date() + "");
			erepo.save(eq);
			SmsSender ss = new SmsSender();
			ss.sendSms(enquiryDto.getContactno());
			redirectAttributes.addFlashAttribute("message", "Your Enquiry Submitted Successfully");
			return "redirect:/contact";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Something Went Wrong");
			return "redirect:/contact";
		}
	}

	@PostMapping("/apply")
	public String submitStudentInfo(@ModelAttribute StudentInfoDto studentInfoDto, BindingResult result,
			RedirectAttributes redirectAttributes) {
		try {
			StudentInfo si = new StudentInfo();
			si.setEnrollmentno(studentInfoDto.getEnrollmentno());
			si.setName(studentInfoDto.getName());
			si.setContactno(studentInfoDto.getContactno());
			si.setWhatsappno(studentInfoDto.getWhatsappno());
			si.setEmailaddress(studentInfoDto.getEmailaddress());
			si.setPassword(studentInfoDto.getPassword());
			si.setCollegename(studentInfoDto.getCollegename());
			si.setCourse(studentInfoDto.getCourse());
			si.setBranch(studentInfoDto.getBranch());
			si.setYear(studentInfoDto.getYear());
			si.setHighschool(studentInfoDto.getHighschool());
			si.setIntermediate(studentInfoDto.getIntermediate());
			si.setAggregatemarks(studentInfoDto.getAggregatemarks());
			si.setTrainingmode(studentInfoDto.getTrainingmode());
			si.setTraininglocation(studentInfoDto.getTraininglocation());
			si.setRegdate(new Date() + "");
			sirepo.save(si);
			redirectAttributes.addFlashAttribute("message", "Your Information Submitted Successfully");
			return "redirect:/apply";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Something Went Wrong");
			return "redirect:/apply";
		}
	}
	
	@PostMapping("/login")
	public String validateStudent(@ModelAttribute StudentInfoDto dto, HttpSession session,RedirectAttributes attrib) {
		 try {
			 StudentInfo s=sirepo.getById(dto.getEmailaddress());
			 if(s.getPassword().equals(dto.getPassword())) {
				 session.setAttribute("studentId", s.getEmailaddress());
				 return "redirect:/student/";
			 }
			 else {
				 attrib.addFlashAttribute("msg","Invalid User");
			 }
			 return "redirect:/login";
		 }
		 catch(EntityNotFoundException e) {
			 attrib.addFlashAttribute("msg","User doesn't Exist");
			 return "redirect:/login";
		 }
	}
	
	
	@PostMapping("/adminLogin")
	public String validateAdmin(@ModelAttribute AdminInfoDto dto, HttpSession session,RedirectAttributes attrib) {
		 try {
			 AdminInfo a=adminrepo.getById(dto.getUserid());
			 if(a.getPassword().equals(dto.getPassword())) {
				 session.setAttribute("adminId", a.getUserid());
				 return "redirect:/admin/";
			 }
			 else {
				 attrib.addFlashAttribute("msg","Invalid Admin");
			 }
			 return "redirect:/adminLogin";
		 }
		 catch(EntityNotFoundException e) {
			 attrib.addFlashAttribute("msg","Admin doesn't Exist");
			 return "redirect:/adminLogin";
		 }
	}
}