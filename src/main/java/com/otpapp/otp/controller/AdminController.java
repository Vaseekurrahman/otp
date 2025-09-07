package com.otpapp.otp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.otpapp.otp.dto.QbDto;
import com.otpapp.otp.model.AdminInfo;
import com.otpapp.otp.model.Enquiry;
import com.otpapp.otp.model.Qb;
import com.otpapp.otp.model.Response;
import com.otpapp.otp.model.Result;
import com.otpapp.otp.model.StudentInfo;
import com.otpapp.otp.service.AdminInfoRepo;
import com.otpapp.otp.service.EnquiryRepo;
import com.otpapp.otp.service.QbRepo;
import com.otpapp.otp.service.ResponseRepo;
import com.otpapp.otp.service.ResultRepo;
import com.otpapp.otp.service.StudentInfoRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	StudentInfoRepo srepo;
	@Autowired
	AdminInfoRepo adminrepo;
	@Autowired
	EnquiryRepo erepo;
	@Autowired
	ResponseRepo resrepo;
	@Autowired
	QbRepo qbrepo;
	@Autowired
	ResultRepo rrepo;

	@GetMapping("/")
	public String showAdminHome(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				long stdcount = srepo.count();
				long ecount = erepo.count();
				long rcount = rrepo.count();
				long fcount = resrepo.count();
				long ccount = resrepo.count();
				model.addAttribute("stdcount", stdcount);
				model.addAttribute("ecount", ecount);
				model.addAttribute("rcount", rcount);
				model.addAttribute("fcount", fcount);
				model.addAttribute("ccount", ccount);
				return "admin/adminHome";
			} else {
				return "redirect:/adminLogin";
			}
		} catch (Exception e) {
			return "redirect:/adminLogin";
		}
	}

	@GetMapping("/adminHome")
	public String showDashboard(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				long stdcount = srepo.count();
				long ecount = erepo.count();
				long rcount = rrepo.count();
				long fcount = resrepo.count();
				long ccount = resrepo.count();
				model.addAttribute("stdcount", stdcount);
				model.addAttribute("ecount", ecount);
				model.addAttribute("rcount", rcount);
				model.addAttribute("fcount", fcount);
				model.addAttribute("ccount", ccount);
				return "admin/adminHome";
			} else {
				return "redirect:/adminLogin";
			}
		} catch (Exception e) {
			return "redirect:/adminLogin";
		}
	}

	@GetMapping("/viewstudent")
	public String showStudents(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				List<StudentInfo> slist = srepo.findAll();
				model.addAttribute("slist", slist);
				return "admin/viewStudent";
			} else {
				return "redirect:/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/adminHome";
		}
	}

	@GetMapping("/viewEnquiry")
	public String showEnquiries(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				List<Enquiry> elist = erepo.findAll();
				model.addAttribute("elist", elist);
				return "admin/viewEnquiry";
			} else {
				return "redirect:/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/adminHome";
		}
	}

	@GetMapping("/viewFeedback")
	public String showFeedbacks(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				List<Response> flist = resrepo.FindResponseByResponseType("Feedback");
				model.addAttribute("flist", flist);
				return "admin/viewFeedback";
			} else {
				return "redirect:/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/adminHome";
		}
	}

	@GetMapping("/viewComplaint")
	public String showComplaints(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				List<Response> clist = resrepo.FindResponseByResponseType("Complaint");
				model.addAttribute("clist", clist);
				return "admin/viewComplaint";
			} else {
				return "redirect:/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/adminHome";
		}
	}

	@GetMapping("/logout")
	public String Logout(HttpSession session) {
		session.invalidate();
		return "redirect:/adminLogin";
	}

	@GetMapping("/changepassword")
	public String showChangePassword(HttpSession session, HttpServletResponse response) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				return "admin/changepassword";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

	@PostMapping("/changepassword")
	public String changePassword(HttpSession session, HttpServletResponse response, HttpServletRequest request,
			RedirectAttributes attrib) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				AdminInfo a = adminrepo.getById(session.getAttribute("adminId").toString());
				String oldpassword = request.getParameter("oldpassword");
				String newpassword = request.getParameter("newpassword");
				String confirmpassword = request.getParameter("confirmpassword");
				if (!newpassword.equals(confirmpassword)) {
					attrib.addFlashAttribute("msg", "New Password and Confirm Password does not matched");
					return "redirect:/admin/changepassword";
				}
				if (!oldpassword.equals(a.getPassword())) {
					attrib.addFlashAttribute("msg", "Old Password does not matched");
					return "redirect:/admin/changepassword";
				}
				a.setPassword(confirmpassword);
				adminrepo.save(a);
				return "redirect:/admin/logout";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

	@GetMapping("/viewstudent/delete")
	public String DeleteStudent(@RequestParam String email, HttpSession session, RedirectAttributes attrib) {
		try {
			if (session.getAttribute("adminId") != null) {
				StudentInfo st = srepo.findById(email).get();
				srepo.delete(st);
				attrib.addFlashAttribute("msg", email + " has been deleted Successfully");
				return "redirect:/admin/viewstudent";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

	@GetMapping("/addQb")
	public String showAddQb(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				QbDto dto = new QbDto();
				model.addAttribute("dto", dto);
				return "admin/addQb";
			} else {
				return "redirect:/adminLogin";
			}
		} catch (Exception e) {
			return "redirect:/adminLogin";
		}
	}

	@PostMapping("/addQb")
	public String addQuestionBank(HttpSession session, HttpServletResponse response, Model model,
			@ModelAttribute QbDto dto, RedirectAttributes attrib) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				Qb qb = new Qb();
				qb.setYear(dto.getYear());
				qb.setQuestion(dto.getQuestion());
				qb.setA(dto.getA());
				qb.setB(dto.getB());
				qb.setC(dto.getC());
				qb.setD(dto.getD());
				qb.setCorrect(dto.getCorrect());
				qbrepo.save(qb);
				attrib.addFlashAttribute("msg", "Question added Successfully");
				return "redirect:/admin/addQb";
			} else {
				return "redirect:/adminLogin";
			}
		} catch (Exception e) {
			return "redirect:/adminLogin";
		}
	}

	@GetMapping("/viewQb")
	public String manageQuestionBank(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				List<Qb> qlist = qbrepo.findAll();
				model.addAttribute("qlist", qlist);
				return "admin/viewQb";
			} else {
				return "redirect:/adminLogin";
			}
		} catch (Exception e) {
			return "redirect:/adminLogin";
		}
	}

	@GetMapping("/viewQb/delete")
	public String DeleteQuestionBank(@RequestParam int id, HttpSession session, RedirectAttributes attrib) {
		try {
			if (session.getAttribute("adminId") != null) {
				Qb qb = qbrepo.findById(id).get();
				qbrepo.delete(qb);
				attrib.addFlashAttribute("msg", "Question has been deleted Successfully");
				return "redirect:/admin/viewQb";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

	@GetMapping("/viewComplaint/delete")
	public String DeleteComplaint(@RequestParam int resid, HttpSession session, RedirectAttributes attrib) {
		try {
			if (session.getAttribute("adminId") != null) {
				Response comp = resrepo.findById(resid).get();
				resrepo.delete(comp);
				attrib.addFlashAttribute("msg", "Complaint has been deleted Successfully");
				return "redirect:/admin/viewComplaint";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

	@GetMapping("/viewfeedback/delete")
	public String DeleteFeedback(@RequestParam int resid, HttpSession session, RedirectAttributes attrib) {
		try {
			if (session.getAttribute("adminId") != null) {
				Response feed = resrepo.findById(resid).get();
				resrepo.delete(feed);
				attrib.addFlashAttribute("msg", "Feedback has been deleted Successfully");
				return "redirect:/admin/viewFeedback";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

	@GetMapping("/viewEnquiry/delete")
	public String DeleteEnquiry(@RequestParam int id, HttpSession session, RedirectAttributes attrib) {
		try {
			if (session.getAttribute("adminId") != null) {
				Enquiry enq = erepo.findById(id).get();
				erepo.delete(enq);
				attrib.addFlashAttribute("msg", "Enquiry has been deleted Successfully");
				return "redirect:/admin/viewEnquiry";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

	@GetMapping("/manageResults")
	public String showResults(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("adminId") != null) {
				List<Result> reslist = rrepo.findAll();
				model.addAttribute("reslist", reslist);
				return "admin/manageResults";
			} else {
				return "redirect:/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/adminHome";
		}
	}

	@GetMapping("/manageResults/delete")
	public String DeleteResult(@RequestParam String email, HttpSession session, RedirectAttributes attrib) {
		try {
			if (session.getAttribute("adminId") != null) {
				Result r = rrepo.findById(email).get();
				rrepo.delete(r);
				attrib.addFlashAttribute("msg", "Result has been deleted Successfully");
				return "redirect:/admin/manageResults";
			} else {
				return "redirect:/admin/adminHome";
			}
		} catch (Exception e) {
			return "redirect:/admin/adminHome";
		}
	}

}