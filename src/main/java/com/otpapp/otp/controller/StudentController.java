package com.otpapp.otp.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.otpapp.otp.dto.ResponseDto;
import com.otpapp.otp.dto.StudentInfoDto;
import com.otpapp.otp.model.Qb;
import com.otpapp.otp.model.Response;
import com.otpapp.otp.model.Result;
import com.otpapp.otp.model.StudentInfo;
import com.otpapp.otp.service.QbRepo;
import com.otpapp.otp.service.ResponseRepo;
import com.otpapp.otp.service.ResultRepo;
import com.otpapp.otp.service.StudentInfoRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Autowired
	StudentInfoRepo srepo;
	@Autowired
	ResponseRepo rrepo;
	@Autowired
	QbRepo qbrepo;
	@Autowired
	ResultRepo resrepo;

	@GetMapping("/")
	public String showStudentHome(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentId") != null) {
				StudentInfo sinfo = srepo.findById(session.getAttribute("studentId").toString()).get();
				model.addAttribute("sinfo", sinfo);
				StudentInfoDto dto = new StudentInfoDto();
				model.addAttribute("dto", dto);
				return "student/studentDashboard";
			} else {
				return "redirect:/login";
			}
		} catch (Exception e) {
			return "redirect:/login";
		}
	}
	
	@GetMapping("/changepassword")
	public String showChangePassword(HttpSession session, HttpServletResponse response) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentId") != null) {
				return "student/changepassword";
			} else {
				return "redirect:/student/studentDashboard";
			}
		} catch (Exception e) {
			return "redirect:/student/studentDashboard";
		}
	}
	
	
	@GetMapping("/videos")
	public String showVideos(HttpSession session, HttpServletResponse response) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentId") != null) {
				return "student/trainingVideos";
			} else {
				return "redirect:/student/studentDashboard";
			}
		} catch (Exception e) {
			return "redirect:/student/studentDashboard";
		}
	}

	@GetMapping("/response")
	public String showResponse(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentId") != null) {
				ResponseDto dto = new ResponseDto();
				model.addAttribute("dto", dto);
				return "student/response";
			} else {
				return "redirect:/student/studentDashboard";
			}
		} catch (Exception e) {
			return "redirect:/student/studentDashboard";
		}
	}

	@PostMapping("/response")
	public String submitResponse(HttpSession session, Model model, @ModelAttribute ResponseDto responseDto,
			RedirectAttributes attrib, HttpServletResponse response) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentId") != null) {
				StudentInfo std = srepo.getById(session.getAttribute("studentId").toString());
				attrib.addFlashAttribute("msg", "Response Submitted Successfully");
				model.addAttribute("studentId", session.getAttribute("userid"));
				Response res = new Response();
				res.setName(std.getName());
				res.setEnrollmentno(std.getEnrollmentno());
				res.setEmailaddress(std.getEmailaddress());
				res.setContactno(std.getContactno());
				res.setResponsetype(responseDto.getResponsetype());
				res.setSubject(responseDto.getSubject());
				res.setMessage(responseDto.getMessage());
				res.setResdate(new Date() + "");
				rrepo.save(res);
				return "redirect:/student/response";
			} else {
				attrib.addFlashAttribute("msg", "Something Went Wrong");
				return "redirect:/student/response";
			}

		} catch (Exception e) {
			attrib.addFlashAttribute("msg", "Something Went Wrong");
			return "redirect:/student/response";
		}
	}

	@GetMapping("/studentDashboard")
	public String showDashboard(HttpSession session, HttpServletResponse response,Model model) {

		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		try {
			if (session.getAttribute("studentId") != null) {
				StudentInfo sinfo = srepo.findById(session.getAttribute("studentId").toString()).get();
				model.addAttribute("sinfo", sinfo);
				StudentInfoDto dto = new StudentInfoDto();
				model.addAttribute("dto", dto);
				return "student/studentDashboard";
			} else {
				return "redirect:/login";
			}
		} catch (Exception e) {
			return "redirect:/login";
		}
	}
	
	@PostMapping("/studentDashboard")
	public String UploadPic(@ModelAttribute StudentInfoDto sdto, HttpSession session, HttpServletResponse response, Model model, RedirectAttributes attrib) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if(session.getAttribute("studentId") != null) {
				MultipartFile filedata = sdto.getProfilepic();
				String storageFileName = filedata.getOriginalFilename();
				String uploadDir = "public/user/";
				Path uploadPath = Paths.get(uploadDir);
				if(!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				try(InputStream inputStream = filedata.getInputStream()){
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				
				StudentInfo std = srepo.findById(session.getAttribute("studentId").toString()).get();
				std.setProfilepic(storageFileName);
				srepo.save(std);
				attrib.addFlashAttribute("msg", "Profile Pic Updated Successfully");
				return "redirect:/student/studentDashboard";
			}
			else {
				return "redirect:/login";
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "redirect:/student/studentDashboard";
		}
	}

	@PostMapping("/changepassword")
	public String changePassword(HttpSession session, HttpServletResponse response, HttpServletRequest request,
			RedirectAttributes attrib) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentId") != null) {
				StudentInfo s = srepo.getById(session.getAttribute("studentId").toString());
				String oldpassword = request.getParameter("oldpassword");
				String newpassword = request.getParameter("newpassword");
				String confirmpassword = request.getParameter("confirmpassword");
				if (!newpassword.equals(confirmpassword)) {
					attrib.addFlashAttribute("msg", "New Password and Confirm Password does not matched");
					return "redirect:/student/changepassword";
				}
				if (!oldpassword.equals(s.getPassword())) {
					attrib.addFlashAttribute("msg", "Old Password does not matched");
					return "redirect:/student/changepassword";
				}
				s.setPassword(confirmpassword);
				srepo.save(s);
				return "redirect:/student/logout";
			} else {
				return "redirect:/student/studentDashboard";
			}
		} catch (Exception e) {
			return "redirect:/student/studentDashboard";
		}
	}

	@GetMapping("/logout")
	public String Logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
	
	
	@GetMapping("/givetest")
	public String showgiveTest(HttpSession session, HttpServletResponse response,Model model) {

		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		try {
			if (session.getAttribute("studentId") != null) {
				return "student/givetest";
			} else {
				return "redirect:/login";
			}
		} catch (Exception e) {
			return "redirect:/login";
		}
	}
	
	@PostMapping("/starttest")
	public String showStartTest(HttpSession session, HttpServletResponse response,Model model, RedirectAttributes attrib) {
		
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentId") != null) {
				StudentInfo sinfo = srepo.findById(session.getAttribute("studentId").toString()).get();
				model.addAttribute("sinfo", sinfo);
				String status = resrepo.getStatus(sinfo.getEmailaddress());
				try {
				if(status.equals("true")) {
					attrib.addFlashAttribute("msg", "You have already given the test");
					return "redirect:/student/givetest";
				}
				else {
					String year = sinfo.getYear();
					List<Qb> qlist = qbrepo.FindByYear(year);
					Gson gson = new Gson();
					String json = gson.toJson(qlist);
					model.addAttribute("json", json);
					model.addAttribute("tt", qlist.size() / 2);
					model.addAttribute("tq", qlist.size());
					return "student/starttest";
				}
				}
				catch(Exception e) {
					String year = sinfo.getYear();
					List<Qb> qlist = qbrepo.FindByYear(year);
					Gson gson = new Gson();
					String json = gson.toJson(qlist);
					model.addAttribute("json", json);
					model.addAttribute("tt", qlist.size() / 2);
					model.addAttribute("tq", qlist.size());
					return "student/starttest";
				}
				
			} else {
				return "redirect:/login";
			}
		} catch (Exception e) {
			return "redirect:/login";
		}
	}
	
	@GetMapping("/testover")
	public String showTestOver(HttpSession session, HttpServletResponse response, Model model, @RequestParam int s, @RequestParam int t) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if(session.getAttribute("studentId")!=null) {
				StudentInfo si = srepo.getById(session.getAttribute("studentId").toString());
				Result rs=new Result();
				rs.setEmailaddress(si.getEmailaddress());
				rs.setName(si.getName());
				rs.setCollegename(si.getCollegename());
				rs.setCourse(si.getCourse());
				rs.setBranch(si.getBranch());
				rs.setYear(si.getYear());
				rs.setContactno(si.getContactno());
				rs.setTotalmarks(t);
				rs.setGetmarks(s);
				rs.setStatus("true");
				resrepo.save(rs);
				return "student/testover";
				
			}
			else {
				return "redirect:/login";
			}
			
		}
		catch(Exception e){
			return "redirect:/login";
		}
		
	}
	
}