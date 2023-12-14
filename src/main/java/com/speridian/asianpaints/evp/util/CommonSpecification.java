package com.speridian.asianpaints.evp.util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.speridian.asianpaints.evp.constants.ActivityType;
import com.speridian.asianpaints.evp.constants.Constants;
import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.constants.TimeRequired;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFeedback;
import com.speridian.asianpaints.evp.entity.ActivityPicture;
import com.speridian.asianpaints.evp.entity.ActivityPromotion;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonSpecification {

	private CommonSpecification() {

	}

	public static Specification<Activity> activitySpecification(SearchCriteria searchCriteria,
			ActivityType activityType) {

		return new Specification<Activity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;

				Predicate createdPredicate = null;

				Predicate publishedPredicate = null;

				Predicate timeRequiredPredicate = null;

				Predicate usernamePredicate = null;

				Predicate activityIdPredicate = null;

				Predicate currentDatePredicate = null;

				LocalDateTime startDate = null;
				LocalDateTime endDate = null;

				try {
					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}

					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}


					if (Optional.ofNullable(searchCriteria.getThemeNameId()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("themeNameId").in(searchCriteria.getThemeNameId()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipationId()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("modeOfParticipationId").in(searchCriteria.getModeOfParticipationId()));
					}

					if (Optional.ofNullable(searchCriteria.getTagIds()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("tagId").in(searchCriteria.getTagIds()));
					}


					if (Optional.ofNullable(searchCriteria.getActivityId()).isPresent()) {
						activityIdPredicate = criteriaBuilder
								.and(root.get("activityId").in(searchCriteria.getActivityIds()));
					}

					switch (activityType) {
					case ONGOING:
						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
						}
						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDatePredicate = criteriaBuilder.lessThan(root.get("endDate"), endDate);
						}
						createdPredicate = criteriaBuilder.equal(root.get("createdActivity"), false);
						publishedPredicate = criteriaBuilder.equal(root.get("published"), true);
						break;
					case UPCOMING:

						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDatePredicate = criteriaBuilder.greaterThan(root.get("endDate"), endDate);
						}
						createdPredicate = criteriaBuilder.equal(root.get("createdActivity"), false);
						publishedPredicate = criteriaBuilder.equal(root.get("published"), true);
						break;

					case PAST:

						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
						}
						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							startDatePredicate = criteriaBuilder.lessThan(root.get("startDate"), endDate);
						}

						currentDatePredicate = criteriaBuilder.lessThan(root.get("startDate"), LocalDateTime.now());
						createdPredicate = criteriaBuilder.equal(root.get("createdActivity"), false);
						publishedPredicate = criteriaBuilder.equal(root.get("published"), true);
						break;

					case CREATED:

						createdPredicate = criteriaBuilder.equal(root.get("createdActivity"), true);
						publishedPredicate = criteriaBuilder.equal(root.get("published"), false);
						usernamePredicate = criteriaBuilder.equal(root.get("createdBy"), searchCriteria.getUsername());
						break;

					default:
						break;
					}

					if (Optional.ofNullable(createdPredicate).isPresent()) {
						predicateList.add(createdPredicate);
					}

					if (Optional.ofNullable(publishedPredicate).isPresent()) {
						predicateList.add(publishedPredicate);
					}

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}

					if (Optional.ofNullable(searchCriteria.getTimeRequired()).isPresent()) {

						timeRequiredPredicate = getTimeRequiredPredicate(searchCriteria, root, criteriaBuilder);

						predicateList.add(timeRequiredPredicate);

					}

					if (Optional.ofNullable(createdPredicate).isPresent()) {
						predicateList.add(createdPredicate);
					} else if (Optional.ofNullable(publishedPredicate).isPresent()) {
						predicateList.add(publishedPredicate);
					}

					if (Optional.ofNullable(usernamePredicate).isPresent()) {
						predicateList.add(usernamePredicate);
					}

					if (Optional.ofNullable(activityIdPredicate).isPresent()) {
						predicateList.add(activityIdPredicate);
					}
					if (Optional.ofNullable(currentDatePredicate).isPresent()) {
						predicateList.add(currentDatePredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return combinedPredicate;
			}

			private Predicate getTimeRequiredPredicate(SearchCriteria searchCriteria, Root<Activity> root,
					CriteriaBuilder criteriaBuilder) {
				if (Optional.ofNullable(searchCriteria.getTimeRequired()).isPresent()) {

					Optional<TimeRequired> timeRequiredOpt = Arrays.stream(TimeRequired.values()).filter(
							timeRequired -> timeRequired.getTimeRequired().equals(searchCriteria.getTimeRequired()))
							.findFirst();

					if (timeRequiredOpt.isPresent()) {
						TimeRequired timeRequired = timeRequiredOpt.get();
						String[] time = timeRequired.getTimeRequired().split("-");
						switch (timeRequired) {
						case ZEROT0TWO:

							
							Predicate twoHoursEquals= criteriaBuilder.and(criteriaBuilder.equal(root.get("timeRequiredHours"), time[1].trim()),
							criteriaBuilder.equal(root.get("timeRequiredMinutes"), 0));
							Predicate lessThantwoHours=criteriaBuilder.and(
									criteriaBuilder.lessThan(root.get("timeRequiredHours"), time[1].trim()),
									criteriaBuilder.between(root.get("timeRequiredMinutes"), 0, 60));
							return criteriaBuilder.or(twoHoursEquals,lessThantwoHours);

						case TWOTO4:
						case FOURTO6:
						case SIXO8:

							Predicate lessthanFirst=criteriaBuilder.and(
									criteriaBuilder.greaterThanOrEqualTo(root.get("timeRequiredHours"), time[0].trim()),
									criteriaBuilder.lessThan(root.get("timeRequiredHours"), time[1].trim()),
									criteriaBuilder.between(root.get("timeRequiredMinutes"), 1, 60));
							
							Predicate lessthanEqualToSecond=criteriaBuilder.and(
									criteriaBuilder.equal(root.get("timeRequiredHours"), time[1].trim()),
									criteriaBuilder.equal(root.get("timeRequiredMinutes"), 0));
							return criteriaBuilder.or(lessthanFirst,lessthanEqualToSecond);
						case ABOVE8:
							String[] timeR=timeRequired.getTimeRequired().split(" ");
							return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("timeRequiredHours"), timeR[1].trim()),
									criteriaBuilder.between(root.get("timeRequiredMinutes"), 1, 60));
						
						default:
							return null;
						}

					}

				}
				return null;
			}

		};

	}

	public static Specification<Activity> activitySpecification(SearchCriteria searchCriteria) {

		return new Specification<Activity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;
				
				Predicate activityNamePredicate = null;
				
				Predicate combinedSearchPredicate=null;

				Predicate createdPredicate = null;

				Predicate publishedPredicate = null;

				Predicate timeRequiredPredicate = null;

				Predicate usernamePredicate = null;

				Predicate activityIdPredicate = null;

				LocalDateTime startDate = null;
				LocalDateTime endDate = null;
				List<String> fieldValues=null;
				String fieldValueToSearch=searchCriteria.getFieldValueToSearch();
				if(Optional.ofNullable(fieldValueToSearch).isPresent()) {
					fieldValues= Arrays.asList(fieldValueToSearch.split(","));
					
					
					if(fieldValues.size()>1) {
						activityIdPredicate = criteriaBuilder
								.and(root.get("activityId").in(fieldValues));
						activityNamePredicate=criteriaBuilder
								.and(root.get("activityName").in(fieldValues));
						
					}else {
						
						String fieldLikeValue="%"+fieldValueToSearch+"%";
						
						activityIdPredicate = criteriaBuilder
								.like(root.get("activityId"),fieldLikeValue);
						activityNamePredicate=criteriaBuilder
								.like(root.get("activityName"),fieldLikeValue);
						
					}
					combinedSearchPredicate=criteriaBuilder.or(activityIdPredicate,activityNamePredicate);
				}
				
				

				try {
					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}

					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}



					if (Optional.ofNullable(searchCriteria.getThemeNameId()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("themeNameId").in(searchCriteria.getThemeNameId()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipationId()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("modeOfParticipationId").in(searchCriteria.getModeOfParticipationId()));
					}

					if (Optional.ofNullable(searchCriteria.getTagIds()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("tagId").in(searchCriteria.getTagIds()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityId()).isPresent()) {
						activityIdPredicate = criteriaBuilder
								.and(root.get("activityId").in(searchCriteria.getActivityIds()));
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
						startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
					}
					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDatePredicate = criteriaBuilder.lessThan(root.get("endDate"), endDate);
					}

					if (Optional.ofNullable(createdPredicate).isPresent()) {
						predicateList.add(createdPredicate);
					}

					if (Optional.ofNullable(publishedPredicate).isPresent()) {
						predicateList.add(publishedPredicate);
					}

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}

					if (Optional.ofNullable(searchCriteria.getTimeRequired()).isPresent()) {

						timeRequiredPredicate = getTimeRequiredPredicate(searchCriteria, root, criteriaBuilder);
						if(Optional.ofNullable(timeRequiredPredicate).isPresent()) {
							predicateList.add(timeRequiredPredicate);
						}
						

					}

					if (Optional.ofNullable(createdPredicate).isPresent()) {
						predicateList.add(createdPredicate);
					} else if (Optional.ofNullable(publishedPredicate).isPresent()) {
						predicateList.add(publishedPredicate);
					}

					if (Optional.ofNullable(usernamePredicate).isPresent()) {
						predicateList.add(usernamePredicate);
					}

					if (!Optional.ofNullable(combinedSearchPredicate).isPresent() && Optional.ofNullable(activityIdPredicate).isPresent()) {
						predicateList.add(activityIdPredicate);
					}
					
					
					
					if(Optional.ofNullable(combinedSearchPredicate).isPresent()) {
						predicateList.add(combinedSearchPredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return combinedPredicate;
			}

			private Predicate getTimeRequiredPredicate(SearchCriteria searchCriteria, Root<Activity> root,
					CriteriaBuilder criteriaBuilder) {
				if (Optional.ofNullable(searchCriteria.getTimeRequired()).isPresent()) {

					Optional<TimeRequired> timeRequiredOpt = Arrays.stream(TimeRequired.values()).filter(
							timeRequired -> timeRequired.getTimeRequired().equals(searchCriteria.getTimeRequired()))
							.findFirst();

					if (timeRequiredOpt.isPresent()) {
						TimeRequired timeRequired = timeRequiredOpt.get();
						if(!timeRequired.equals(TimeRequired.ALL)){
							String[] time = timeRequired.getTimeRequired().split("-");
							switch (timeRequired) {
							case ZEROT0TWO:

								
								Predicate twoHoursEquals= criteriaBuilder.and(criteriaBuilder.equal(root.get("timeRequiredHours"), time[1].trim()),
								criteriaBuilder.equal(root.get("timeRequiredMinutes"), 0));
								Predicate lessThantwoHours=criteriaBuilder.and(
										criteriaBuilder.lessThan(root.get("timeRequiredHours"), time[1].trim()),
										criteriaBuilder.between(root.get("timeRequiredMinutes"), 0, 60));
								return criteriaBuilder.or(twoHoursEquals,lessThantwoHours);

							case TWOTO4:
							case FOURTO6:
							case SIXO8:

								Predicate lessthanFirst=criteriaBuilder.and(
										criteriaBuilder.greaterThanOrEqualTo(root.get("timeRequiredHours"), time[0].trim()),
										criteriaBuilder.lessThan(root.get("timeRequiredHours"), time[1].trim()),
										criteriaBuilder.between(root.get("timeRequiredMinutes"), 1, 60));
								
								Predicate lessthanEqualToSecond=criteriaBuilder.and(
										criteriaBuilder.equal(root.get("timeRequiredHours"), time[1].trim()),
										criteriaBuilder.equal(root.get("timeRequiredMinutes"), 0));
								return criteriaBuilder.or(lessthanFirst,lessthanEqualToSecond);
							case ABOVE8:
								String[] timeR=timeRequired.getTimeRequired().split(" ");
								return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("timeRequiredHours"), timeR[1].trim()),
										criteriaBuilder.between(root.get("timeRequiredMinutes"), 1, 60));
							
							default:
								return null;
							}

						}else {
							return null;
						}
						}
					
						

				}
				return null;
			}

		};

	}

	public static Specification<Activity> allActivitySpecification(SearchCriteria searchCriteria) {

		return new Specification<Activity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate activityUUidPredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;

				Predicate publishedPredicate = null;

				Predicate timeRequiredPredicate = null;

				LocalDateTime startDate = null;
				LocalDateTime endDate = null;

				try {
					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}

					if (Optional.ofNullable(searchCriteria.getTimeRequired()).isPresent()) {

						timeRequiredPredicate = getTimeRequiredPredicate(searchCriteria, root, criteriaBuilder);

					}

					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}


					if (Optional.ofNullable(searchCriteria.getThemeNameId()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("themeNameId").in(searchCriteria.getThemeNameId()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipationId()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("modeOfParticipationId").in(searchCriteria.getModeOfParticipationId()));
					}

					if (Optional.ofNullable(searchCriteria.getTagIds()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("tagId").in(searchCriteria.getTagIds()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityId").in(searchCriteria.getActivityIds()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityUUID()).isPresent()) {
						activityUUidPredicate = criteriaBuilder
								.and(root.get("activityUUID").in(searchCriteria.getActivityUUID()));
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
						startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
					}
					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDatePredicate = criteriaBuilder.lessThan(root.get("endDate"), endDate);
					}

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}

					if (Optional.ofNullable(activityUUidPredicate).isPresent()) {
						predicateList.add(activityUUidPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}

					if (Optional.ofNullable(timeRequiredPredicate).isPresent()) {
						predicateList.add(timeRequiredPredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return combinedPredicate;

			}

			private Predicate getTimeRequiredPredicate(SearchCriteria searchCriteria, Root<Activity> root,
					CriteriaBuilder criteriaBuilder) {
				if (Optional.ofNullable(searchCriteria.getTimeRequired()).isPresent()) {

					Optional<TimeRequired> timeRequiredOpt = Arrays.stream(TimeRequired.values()).filter(
							timeRequired -> timeRequired.getTimeRequired().equals(searchCriteria.getTimeRequired()))
							.findFirst();

					if (timeRequiredOpt.isPresent()) {
						TimeRequired timeRequired = timeRequiredOpt.get();
						String[] time = timeRequired.getTimeRequired().split("-");
						switch (timeRequired) {
						case ZEROT0TWO:

							
							Predicate twoHoursEquals= criteriaBuilder.and(criteriaBuilder.equal(root.get("timeRequiredHours"), time[1].trim()),
							criteriaBuilder.equal(root.get("timeRequiredMinutes"), 0));
							Predicate lessThantwoHours=criteriaBuilder.and(
									criteriaBuilder.lessThan(root.get("timeRequiredHours"), time[1].trim()),
									criteriaBuilder.between(root.get("timeRequiredMinutes"), 0, 60));
							return criteriaBuilder.or(twoHoursEquals,lessThantwoHours);

						case TWOTO4:
						case FOURTO6:
						case SIXO8:

							Predicate lessthanFirst=criteriaBuilder.and(
									criteriaBuilder.greaterThanOrEqualTo(root.get("timeRequiredHours"), time[0].trim()),
									criteriaBuilder.lessThan(root.get("timeRequiredHours"), time[1].trim()),
									criteriaBuilder.between(root.get("timeRequiredMinutes"), 1, 60));
							
							Predicate lessthanEqualToSecond=criteriaBuilder.and(
									criteriaBuilder.equal(root.get("timeRequiredHours"), time[1].trim()),
									criteriaBuilder.equal(root.get("timeRequiredMinutes"), 0));
							return criteriaBuilder.or(lessthanFirst,lessthanEqualToSecond);
						case ABOVE8:
							String[] timeR=timeRequired.getTimeRequired().split(" ");
							return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("timeRequiredHours"), timeR[1].trim()),
									criteriaBuilder.between(root.get("timeRequiredMinutes"), 1, 60));
						
						default:
							return null;
						}

					}

				}
				return null;
			}

		};

	}
	
	
	public static Specification<EmployeeActivityHistory> allActivityParticipationSpecification(SearchCriteria searchCriteria,boolean excludeRejectedByAdmins,boolean dashBoardDetails) {

		return new Specification<EmployeeActivityHistory>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<EmployeeActivityHistory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate activityUUidPredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;
				
				Predicate activityNamePredicate = null;
				
				Predicate employeeIdPredicate = null;
				
				Predicate employeeNamePredicate = null;

				Predicate employeeStatusPredicate=null;

				Predicate combinedSearchPredicate=null;
				
				Predicate excludeRejectedByAdminsPredicate=null;
				
				LocalDateTime startDate = null;
				LocalDateTime endDate = null;
				List<String> fieldValues=null;
				try {
					
					
					String fieldValueToSearch=searchCriteria.getFieldValueToSearch();
					if(Optional.ofNullable(fieldValueToSearch).isPresent()) {
						fieldValues= Arrays.asList(fieldValueToSearch.split(","));
						
						
						if(fieldValues.size()>1) {
							employeeIdPredicate = criteriaBuilder
									.and(root.get("employeeId").in(fieldValues));
							employeeNamePredicate=criteriaBuilder
									.and(root.get("employeeName").in(fieldValues));
							
						}else {
							
							String fieldLikeValue="%"+fieldValueToSearch+"%";
							
							employeeIdPredicate = criteriaBuilder
									.like(root.get("employeeId"),fieldLikeValue);
							employeeNamePredicate=criteriaBuilder
									.like(root.get("employeeName"),fieldLikeValue);
							
						}
						combinedSearchPredicate=criteriaBuilder.or(employeeIdPredicate,employeeNamePredicate);
					}
					
					if((dashBoardDetails || (Optional.ofNullable(searchCriteria.isPastActivity()).isPresent() && searchCriteria.isPastActivity())) && searchCriteria.getStatuses().size()<3) {
						employeeStatusPredicate=criteriaBuilder.or( criteriaBuilder
							.equal(root.get("employeeActivityStatus"),EmployeeActivityStatus.FEEDBACK),criteriaBuilder
							.equal(root.get("employeeActivityStatus"),EmployeeActivityStatus.PARTICIPATED));
					}
					
					else if(Optional.ofNullable(searchCriteria.getStatuses()).isPresent() && !searchCriteria.getStatuses().isEmpty()) {
						
						employeeStatusPredicate=criteriaBuilder
						.and(root.get("employeeActivityStatus").in(searchCriteria.getStatuses()));
					}
					
					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}


					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}



					if (Optional.ofNullable(searchCriteria.getLocations()).isPresent()) {
						locationPredicate = criteriaBuilder
								.and(root.get("activityLocation").in(searchCriteria.getLocations()));
					}
					
					if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeNames()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipations()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
					}

					if (Optional.ofNullable(searchCriteria.getTagNames()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagNames()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityNames()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityName").in(searchCriteria.getActivityNames()));
					}
					
					if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityName").in(searchCriteria.getActivityIds()));
					}
				

					if (Optional.ofNullable(searchCriteria.getActivityUUID()).isPresent()) {
						activityUUidPredicate = criteriaBuilder
								.and(root.get("activityUUID").in(searchCriteria.getActivityUUID()));
					}
					
					if (!Optional.ofNullable(combinedSearchPredicate).isPresent() && Optional.ofNullable(searchCriteria.getEmployeeIds()).isPresent()) {
						employeeIdPredicate = criteriaBuilder
								.and(root.get("employeeId").in(searchCriteria.getEmployeeIds()));
					}

					if (!Optional.ofNullable(combinedSearchPredicate).isPresent() && Optional.ofNullable(searchCriteria.getEmployeeNames()).isPresent()) {
						employeeNamePredicate = criteriaBuilder
								.and(root.get("employeeName").in(searchCriteria.getEmployeeNames()));
					}
					

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
						startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate.toLocalDate());
					}
					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDatePredicate = criteriaBuilder.lessThan(root.get("endDate"), endDate.toLocalDate());
					}
					
					if(excludeRejectedByAdmins) {
						excludeRejectedByAdminsPredicate=criteriaBuilder.equal(root.get("rejectedByAdmin"), Boolean.FALSE);
					}

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}
					
					if (Optional.ofNullable(activityNamePredicate).isPresent()) {
						predicateList.add(activityNamePredicate);
					}

					if (Optional.ofNullable(activityUUidPredicate).isPresent()) {
						predicateList.add(activityUUidPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}
					
					if (!Optional.ofNullable(combinedPredicate).isPresent() && Optional.ofNullable(employeeIdPredicate).isPresent()) {
						predicateList.add(employeeIdPredicate);
					}

					if (!Optional.ofNullable(combinedPredicate).isPresent() && Optional.ofNullable(employeeNamePredicate).isPresent()) {
						predicateList.add(employeeNamePredicate);
					}
					if (Optional.ofNullable(combinedSearchPredicate).isPresent()) {
						predicateList.add(combinedSearchPredicate);
					}
					
					if (Optional.ofNullable(employeeStatusPredicate).isPresent()) {
						predicateList.add(employeeStatusPredicate);
					}
					
					if (Optional.ofNullable(excludeRejectedByAdminsPredicate).isPresent()) {
						predicateList.add(excludeRejectedByAdminsPredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return combinedPredicate;

			}

		};

	}
	
	
	
	
	public static Specification<EmployeeActivityHistory> allActivityParticipationSpecificationForDashBoardHeader(SearchCriteria searchCriteria) {

		return new Specification<EmployeeActivityHistory>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<EmployeeActivityHistory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate activityUUidPredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;
				
				Predicate activityNamePredicate = null;
				
				Predicate employeeIdPredicate = null;
				
				Predicate employeeNamePredicate = null;

				Predicate employeeStatusPredicate=null;

				Predicate combinedSearchPredicate=null;
				
				Predicate excludeRejectedByAdminsPredicate=null;
				
				LocalDateTime startDate = null;
				LocalDateTime endDate = null;
				List<String> fieldValues=null;
				try {
					
					
					String fieldValueToSearch=searchCriteria.getFieldValueToSearch();
					if(Optional.ofNullable(fieldValueToSearch).isPresent()) {
						fieldValues= Arrays.asList(fieldValueToSearch.split(","));
						
						
						if(fieldValues.size()>1) {
							employeeIdPredicate = criteriaBuilder
									.and(root.get("employeeId").in(fieldValues));
							employeeNamePredicate=criteriaBuilder
									.and(root.get("employeeName").in(fieldValues));
							
						}else {
							
							String fieldLikeValue="%"+fieldValueToSearch+"%";
							
							employeeIdPredicate = criteriaBuilder
									.like(root.get("employeeId"),fieldLikeValue);
							employeeNamePredicate=criteriaBuilder
									.like(root.get("employeeName"),fieldLikeValue);
							
						}
						combinedSearchPredicate=criteriaBuilder.or(employeeIdPredicate,employeeNamePredicate);
					}
					
					employeeStatusPredicate = criteriaBuilder
							.and(root.get("employeeActivityStatus").in(searchCriteria.getStatuses()));
					
						
					
					
					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}


					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}



					if (Optional.ofNullable(searchCriteria.getLocations()).isPresent()) {
						locationPredicate = criteriaBuilder
								.and(root.get("activityLocation").in(searchCriteria.getLocations()));
					}
					
					if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeNames()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipations()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
					}

					if (Optional.ofNullable(searchCriteria.getTagNames()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagNames()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityNames()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityName").in(searchCriteria.getActivityNames()));
					}
					
					if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityName").in(searchCriteria.getActivityIds()));
					}
				

					if (Optional.ofNullable(searchCriteria.getActivityUUID()).isPresent()) {
						activityUUidPredicate = criteriaBuilder
								.and(root.get("activityUUID").in(searchCriteria.getActivityUUID()));
					}
					
					if (!Optional.ofNullable(combinedSearchPredicate).isPresent() && Optional.ofNullable(searchCriteria.getEmployeeIds()).isPresent()) {
						employeeIdPredicate = criteriaBuilder
								.and(root.get("employeeId").in(searchCriteria.getEmployeeIds()));
					}

					if (!Optional.ofNullable(combinedSearchPredicate).isPresent() && Optional.ofNullable(searchCriteria.getEmployeeNames()).isPresent()) {
						employeeNamePredicate = criteriaBuilder
								.and(root.get("employeeName").in(searchCriteria.getEmployeeNames()));
					}
					

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
						startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate.toLocalDate());
					}
					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDatePredicate = criteriaBuilder.lessThan(root.get("endDate"), endDate.toLocalDate());
					}
					
						excludeRejectedByAdminsPredicate=criteriaBuilder.equal(root.get("rejectedByAdmin"), Boolean.FALSE);

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}
					
					if (Optional.ofNullable(activityNamePredicate).isPresent()) {
						predicateList.add(activityNamePredicate);
					}

					if (Optional.ofNullable(activityUUidPredicate).isPresent()) {
						predicateList.add(activityUUidPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}
					
					if (!Optional.ofNullable(combinedPredicate).isPresent() && Optional.ofNullable(employeeIdPredicate).isPresent()) {
						predicateList.add(employeeIdPredicate);
					}

					if (!Optional.ofNullable(combinedPredicate).isPresent() && Optional.ofNullable(employeeNamePredicate).isPresent()) {
						predicateList.add(employeeNamePredicate);
					}
					if (Optional.ofNullable(combinedSearchPredicate).isPresent()) {
						predicateList.add(combinedSearchPredicate);
					}
					
					if (Optional.ofNullable(employeeStatusPredicate).isPresent()) {
						predicateList.add(employeeStatusPredicate);
					}
					
					if (Optional.ofNullable(excludeRejectedByAdminsPredicate).isPresent()) {
						predicateList.add(excludeRejectedByAdminsPredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return combinedPredicate;

			}

		};

	}

	public static Specification<ActivityPicture> allActivityPicture(SearchCriteria searchCriteria, boolean creatives) {

		return new Specification<ActivityPicture>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<ActivityPicture> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;

				Predicate imageTypePredicate = null;

				Predicate uploadedByPredicate = null;
				Predicate publishedPredicate = null;

				Predicate imageNamePredicate = null;

				if (Optional.ofNullable(searchCriteria.getImageName()).isPresent()) {
					imageNamePredicate = criteriaBuilder.like(root.get("imageName"),"%"+searchCriteria.getImageName()+"%");
				}

				try {
					if (Optional.ofNullable(searchCriteria.getUsername()).isPresent()) {
						uploadedByPredicate = criteriaBuilder.equal(root.get("uploadedBy"),
								searchCriteria.getUsername());
						publishedPredicate = criteriaBuilder.equal(root.get("published"), Boolean.TRUE);
					}

					Predicate deletePredicate = criteriaBuilder.isNull(root.get("deleted"));

					LocalDateTime startDate = null;
					LocalDateTime endDate = null;

					if (creatives) {
						imageTypePredicate = criteriaBuilder.equal(root.get("imageType"), ImageType.CREATIVE);
					} else {
						imageTypePredicate = criteriaBuilder.equal(root.get("imageType"), ImageType.EMPLOYEE_UPLOAD);
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}

					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}


					if (Optional.ofNullable(searchCriteria.getLocation()).isPresent()) {

						locationPredicate = criteriaBuilder
								.and(root.get("activityLocation").in(searchCriteria.getLocations()));
					}

					if (Optional.ofNullable(searchCriteria.getThemeName()).isPresent() && creatives) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeNames()));
					} else if (Optional.ofNullable(searchCriteria.getThemeName()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeName()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipation()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
					}

					if (Optional.ofNullable(searchCriteria.getTagName()).isPresent() && creatives) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagName()));
					}

					else if (Optional.ofNullable(searchCriteria.getTagName()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagNames()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityPictureId").in(searchCriteria.getActivityIds()));
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
						startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), startDate);
					}
					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDatePredicate = criteriaBuilder.lessThan(root.get("createdOn"), endDate);
					}

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}

					if (Optional.ofNullable(imageTypePredicate).isPresent()) {
						predicateList.add(imageTypePredicate);
					}

					if (Optional.ofNullable(deletePredicate).isPresent()) {
						predicateList.add(deletePredicate);
					}
					if (Optional.ofNullable(uploadedByPredicate).isPresent()) {
						predicateList.add(uploadedByPredicate);
					}

					if (Optional.ofNullable(publishedPredicate).isPresent()) {
						predicateList.add(publishedPredicate);
					}
					if (Optional.ofNullable(imageNamePredicate).isPresent()) {
						predicateList.add(imageNamePredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				return combinedPredicate;

			}

		};

	}
	
	
	
	public static Specification<ActivityPicture> allActivityPicturePublished(SearchCriteria searchCriteria, boolean creatives) {

		return new Specification<ActivityPicture>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<ActivityPicture> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;

				Predicate imageTypePredicate = null;

				Predicate uploadedByPredicate = null;
				Predicate publishedPredicate = null;

				Predicate imageNamePredicate = null;
				
				Predicate uploadedByAdminPredicate=null;

				if (Optional.ofNullable(searchCriteria.getImageName()).isPresent()) {
					imageNamePredicate = criteriaBuilder.like(root.get("imageName"),"%"+searchCriteria.getImageName()+"%");
				}

				try {
					uploadedByAdminPredicate=criteriaBuilder.equal(root.get("uploadedByAdmin"), Boolean.TRUE);
					publishedPredicate = criteriaBuilder.equal(root.get("published"), Boolean.TRUE);

					Predicate deletePredicate = criteriaBuilder.isNull(root.get("deleted"));

					LocalDateTime startDate = null;
					LocalDateTime endDate = null;

					if (creatives) {
						imageTypePredicate = criteriaBuilder.equal(root.get("imageType"), ImageType.CREATIVE);
					} else {
						imageTypePredicate = criteriaBuilder.equal(root.get("imageType"), ImageType.EMPLOYEE_UPLOAD);
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}

					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}


					if (Optional.ofNullable(searchCriteria.getLocation()).isPresent()) {

						locationPredicate = criteriaBuilder
								.and(root.get("activityLocation").in(searchCriteria.getLocations()));
					}

					if (Optional.ofNullable(searchCriteria.getThemeName()).isPresent() && creatives) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeNames()));
					} else if (Optional.ofNullable(searchCriteria.getThemeName()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeName()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipation()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
					}

					if (Optional.ofNullable(searchCriteria.getTagName()).isPresent() && creatives) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagName()));
					}

					else if (Optional.ofNullable(searchCriteria.getTagName()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagNames()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityPictureId").in(searchCriteria.getActivityIds()));
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
						startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), startDate);
					}
					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDatePredicate = criteriaBuilder.lessThan(root.get("createdOn"), endDate);
					}

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}

					if (Optional.ofNullable(imageTypePredicate).isPresent()) {
						predicateList.add(imageTypePredicate);
					}

					if (Optional.ofNullable(deletePredicate).isPresent()) {
						predicateList.add(deletePredicate);
					}
					if (Optional.ofNullable(uploadedByPredicate).isPresent()) {
						predicateList.add(uploadedByPredicate);
					}

					if (Optional.ofNullable(publishedPredicate).isPresent()) {
						predicateList.add(publishedPredicate);
					}
					
					if (Optional.ofNullable(uploadedByAdminPredicate).isPresent()) {
						predicateList.add(uploadedByAdminPredicate);
					}
					
					if (Optional.ofNullable(imageNamePredicate).isPresent()) {
						predicateList.add(imageNamePredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				return combinedPredicate;

			}

		};

	}
	
	
	
	public static Specification<ActivityPicture> allActivityPictureUnPublished(SearchCriteria searchCriteria, boolean creatives) {

		return new Specification<ActivityPicture>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<ActivityPicture> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();
				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate modePredicate = null;

				Predicate tagPredicate = null;

				Predicate activityPredicate = null;

				Predicate imageTypePredicate = null;

				Predicate uploadedByPredicate = null;
				Predicate publishedPredicate = null;

				Predicate imageNamePredicate = null;

				if (Optional.ofNullable(searchCriteria.getImageName()).isPresent()) {
					imageNamePredicate = criteriaBuilder.like(root.get("imageName"),"%"+searchCriteria.getImageName()+"%");
				}

				try {
					Predicate uploadedByAdminPredicate=criteriaBuilder.equal(root.get("uploadedByAdmin"), Boolean.TRUE);
					
					publishedPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("published"), Boolean.FALSE),
							criteriaBuilder.isNull(root.get("published")));

					Predicate deletePredicate = criteriaBuilder.isNull(root.get("deleted"));

					LocalDateTime startDate = null;
					LocalDateTime endDate = null;

					if (creatives) {
						imageTypePredicate = criteriaBuilder.equal(root.get("imageType"), ImageType.CREATIVE);
					} else {
						imageTypePredicate = criteriaBuilder.equal(root.get("imageType"), ImageType.EMPLOYEE_UPLOAD);
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {

						startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());

					}

					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
						endDate = endDate.plusDays(1);
					}


					if (Optional.ofNullable(searchCriteria.getLocation()).isPresent()) {

						locationPredicate = criteriaBuilder
								.and(root.get("activityLocation").in(searchCriteria.getLocations()));
					}

					if (Optional.ofNullable(searchCriteria.getThemeName()).isPresent() && creatives) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeNames()));
					} else if (Optional.ofNullable(searchCriteria.getThemeName()).isPresent()) {
						themePredicate = criteriaBuilder
								.and(root.get("activityTheme").in(searchCriteria.getThemeName()));
					}

					if (Optional.ofNullable(searchCriteria.getModeOfParticipation()).isPresent()) {
						modePredicate = criteriaBuilder
								.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
					}

					if (Optional.ofNullable(searchCriteria.getTagName()).isPresent() && creatives) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagName()));
					}

					else if (Optional.ofNullable(searchCriteria.getTagName()).isPresent()) {
						tagPredicate = criteriaBuilder.and(root.get("activityTag").in(searchCriteria.getTagNames()));
					}

					if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
						activityPredicate = criteriaBuilder
								.and(root.get("activityPictureId").in(searchCriteria.getActivityIds()));
					}

					if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
						startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), startDate);
					}
					if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
						endDatePredicate = criteriaBuilder.lessThan(root.get("createdOn"), endDate);
					}

					if (Optional.ofNullable(locationPredicate).isPresent()) {
						predicateList.add(locationPredicate);
					}
					if (Optional.ofNullable(themePredicate).isPresent()) {
						predicateList.add(themePredicate);
					}

					if (Optional.ofNullable(modePredicate).isPresent()) {
						predicateList.add(modePredicate);
						combinedPredicate = criteriaBuilder.and(modePredicate);
					}
					if (Optional.ofNullable(tagPredicate).isPresent()) {
						predicateList.add(tagPredicate);
					}

					if (Optional.ofNullable(activityPredicate).isPresent()) {
						predicateList.add(activityPredicate);
					}

					if (Optional.ofNullable(startDatePredicate).isPresent()) {
						predicateList.add(startDatePredicate);
					}

					if (Optional.ofNullable(endDatePredicate).isPresent()) {
						predicateList.add(endDatePredicate);
					}

					if (Optional.ofNullable(imageTypePredicate).isPresent()) {
						predicateList.add(imageTypePredicate);
					}

					if (Optional.ofNullable(deletePredicate).isPresent()) {
						predicateList.add(deletePredicate);
					}
					if (Optional.ofNullable(uploadedByPredicate).isPresent()) {
						predicateList.add(uploadedByPredicate);
					}

					if (Optional.ofNullable(publishedPredicate).isPresent()) {
						predicateList.add(publishedPredicate);
					}
					if (Optional.ofNullable(imageNamePredicate).isPresent()) {
						predicateList.add(imageNamePredicate);
					}
					
					if (Optional.ofNullable(uploadedByAdminPredicate).isPresent()) {
						predicateList.add(uploadedByAdminPredicate);
					}

					combinedPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));

					return combinedPredicate;
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				return combinedPredicate;

			}

		};

	}

	public static Specification<ActivityFeedback> getActivityFeedbackSpecification(SearchCriteria searchCriteria) {

		return new Specification<ActivityFeedback>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<ActivityFeedback> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();

				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate ratingPredicate = null;

				Predicate tagPredicate = null;

				Predicate employeeIdPredicate = null;

				Predicate activityNamePredicate = null;

				Predicate createdByPredicate = null;

				Predicate modePredicate = null;

				Predicate uploadedByAdminPredicate = null;

				Predicate deletedByAdminPredicate = null;
				
				Predicate publishOrUnpublishPredicate=null;

				try {

					if (Optional.ofNullable(searchCriteria).isPresent() && !searchCriteria.isEmpty()) {

						if (Optional.ofNullable(searchCriteria.getRole()).isPresent()) {
							if (searchCriteria.getRole().equals(Constants.ROLE_EMPLOYEE)) {
								
								searchCriteria.setDeletedByAdmin(Constants.FALSE);
							} 
						}

						LocalDateTime startDate = null;
						LocalDateTime endDate = null;

						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());
						}

						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
							endDate = endDate.plusDays(1);
						}


						if (Optional.ofNullable(searchCriteria.getUploadedByAdmin()).isPresent()) {
							if (searchCriteria.getUploadedByAdmin().equals(Constants.TRUE)) {
								uploadedByAdminPredicate = criteriaBuilder.equal(root.get("uploadedByAdmin"), true);
							} else {
								uploadedByAdminPredicate = criteriaBuilder.equal(root.get("uploadedByAdmin"), false);
							}
						}

						if (Optional.ofNullable(searchCriteria.getDeletedByAdmin()).isPresent()) {
							if (searchCriteria.getDeletedByAdmin().equals(Constants.TRUE)) {
								deletedByAdminPredicate = criteriaBuilder.equal(root.get("deleted"), true);
							} else {
								deletedByAdminPredicate = criteriaBuilder.equal(root.get("deleted"), false);
							}
						}
						
						if (Optional.ofNullable(searchCriteria.getPublishOrUnpublish()).isPresent()) {

							if(searchCriteria.getPublishOrUnpublish().equals(Constants.TRUE)) {
								publishOrUnpublishPredicate=criteriaBuilder.equal(root.get("published"),
										Boolean.valueOf(searchCriteria.getPublishOrUnpublish()));
							}else {
								publishOrUnpublishPredicate = criteriaBuilder.or(
										criteriaBuilder.isNull(root.get("published")),
										criteriaBuilder.equal(root.get("published"),
												false));
							}
							
							
						}

						if (Optional.ofNullable(searchCriteria.getEmployeeId()).isPresent()) {
							employeeIdPredicate = criteriaBuilder
									.and(root.get("employeeId").in(searchCriteria.getEmployeeIds()));
						}
						
						

						
						

						if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
							activityNamePredicate = criteriaBuilder
									.and(root.get("activityName").in(searchCriteria.getActivityIds()));
						}

						if (Optional.ofNullable(searchCriteria.getRating()).isPresent()) {
							ratingPredicate = criteriaBuilder.and(root.get("rating").in(searchCriteria.getRating()));
						}

						

						if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
							themePredicate = criteriaBuilder
									.and(root.get("themeName").in(searchCriteria.getThemeNames()));
						}

						if (Optional.ofNullable(searchCriteria.getTagNames()).isPresent()) {
							tagPredicate = criteriaBuilder.and(root.get("tagName").in(searchCriteria.getTagNames()));
						}

						if (Optional.ofNullable(searchCriteria.getModeOfParticipations()).isPresent()) {
							modePredicate = criteriaBuilder
									.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
						}
						
						

						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), startDate);
						}
						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDatePredicate = criteriaBuilder.lessThan(root.get("createdOn"), endDate);
						}

						/*
						 * ================================
						 * 
						 */

						if (Optional.ofNullable(uploadedByAdminPredicate).isPresent()) {
							predicateList.add(uploadedByAdminPredicate);
						}

						if (Optional.ofNullable(locationPredicate).isPresent()) {
							predicateList.add(locationPredicate);
						}
						if (Optional.ofNullable(themePredicate).isPresent()) {
							predicateList.add(themePredicate);
						}

						if (Optional.ofNullable(modePredicate).isPresent()) {
							predicateList.add(modePredicate);
							combinedPredicate = criteriaBuilder.and(modePredicate);
						}
						if (Optional.ofNullable(tagPredicate).isPresent()) {
							predicateList.add(tagPredicate);
						}

						if (Optional.ofNullable(startDatePredicate).isPresent()) {
							predicateList.add(startDatePredicate);
						}

						if (Optional.ofNullable(endDatePredicate).isPresent()) {
							predicateList.add(endDatePredicate);
						}

						if (Optional.ofNullable(createdByPredicate).isPresent()) {
							predicateList.add(createdByPredicate);
						}

						if (Optional.ofNullable(employeeIdPredicate).isPresent()) {
							predicateList.add(employeeIdPredicate);
						}

						if (Optional.ofNullable(activityNamePredicate).isPresent()) {
							predicateList.add(activityNamePredicate);
						}

						if (Optional.ofNullable(ratingPredicate).isPresent()) {
							predicateList.add(ratingPredicate);
						}

						if (Optional.ofNullable(deletedByAdminPredicate).isPresent()) {
							predicateList.add(deletedByAdminPredicate);
						}
						
						if (Optional.ofNullable(publishOrUnpublishPredicate).isPresent()) {
							predicateList.add(publishOrUnpublishPredicate);
						}

						combinedPredicate = criteriaBuilder
								.and(predicateList.toArray(new Predicate[predicateList.size()]));

						return combinedPredicate;
					} else {

						if (Optional.ofNullable(searchCriteria.getRole()).isPresent()) {
							if (searchCriteria.getRole().equals(Constants.ROLE_EMPLOYEE)) {
								createdByPredicate = criteriaBuilder.equal(root.get("createdBy"),
										searchCriteria.getUsername());
							}
						}

						combinedPredicate = criteriaBuilder
								.and(predicateList.toArray(new Predicate[predicateList.size()]));

						return combinedPredicate;

					}
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				return combinedPredicate;

			}

		};

	}
	
	
	public static Specification<ActivityFeedback> getActivityFeedbackSpecificationForActivityDetails(SearchCriteria searchCriteria) {

		return new Specification<ActivityFeedback>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<ActivityFeedback> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();

				Predicate combinedPredicate = null;

				Predicate startDatePredicate = null;

				Predicate endDatePredicate = null;

				Predicate locationPredicate = null;

				Predicate themePredicate = null;

				Predicate ratingPredicate = null;

				Predicate tagPredicate = null;

				Predicate employeeIdPredicate = null;

				Predicate activityNamePredicate = null;

				Predicate createdByPredicate = null;

				Predicate modePredicate = null;

				Predicate uploadedByAdminPredicate = null;

				Predicate deletedByAdminPredicate = null;
				
				Predicate publishOrUnpublishPredicate=null;
				
				Predicate manualUploadPredicate=null;

				try {

					if (Optional.ofNullable(searchCriteria).isPresent() && !searchCriteria.isEmpty()) {

						if (Optional.ofNullable(searchCriteria.getRole()).isPresent()) {
							if (searchCriteria.getRole().equals(Constants.ROLE_EMPLOYEE)) {
								manualUploadPredicate=criteriaBuilder.or(criteriaBuilder.equal(root.get("manualUpload"), false),criteriaBuilder.isNull(root.get("manualUpload")));
							} 
						}

						LocalDateTime startDate = null;
						LocalDateTime endDate = null;

						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());
						}

						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
							endDate = endDate.plusDays(1);
						}


						if (Optional.ofNullable(searchCriteria.getUploadedByAdmin()).isPresent()) {
							if (searchCriteria.getUploadedByAdmin().equals(Constants.TRUE)) {
								uploadedByAdminPredicate = criteriaBuilder.equal(root.get("uploadedByAdmin"), true);
							} else {
								uploadedByAdminPredicate = criteriaBuilder.equal(root.get("uploadedByAdmin"), false);
							}
						}

						if (Optional.ofNullable(searchCriteria.getDeletedByAdmin()).isPresent()) {
							if (searchCriteria.getDeletedByAdmin().equals(Constants.TRUE)) {
								deletedByAdminPredicate = criteriaBuilder.equal(root.get("deleted"), true);
							} else {
								deletedByAdminPredicate = criteriaBuilder.equal(root.get("deleted"), false);
							}
						}
						
						if (Optional.ofNullable(searchCriteria.getPublishOrUnpublish()).isPresent()) {

							if(searchCriteria.getPublishOrUnpublish().equals(Constants.TRUE)) {
								publishOrUnpublishPredicate=criteriaBuilder.equal(root.get("published"),
										Boolean.valueOf(searchCriteria.getPublishOrUnpublish()));
							}else {
								publishOrUnpublishPredicate = criteriaBuilder.or(
										criteriaBuilder.isNull(root.get("published")),
										criteriaBuilder.equal(root.get("published"),
												false));
							}
							
							
						}

						if (Optional.ofNullable(searchCriteria.getEmployeeId()).isPresent()) {
							employeeIdPredicate = criteriaBuilder
									.and(root.get("employeeId").in(searchCriteria.getEmployeeIds()));
						}
						
						

						
						

						if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
							activityNamePredicate = criteriaBuilder
									.and(root.get("activityName").in(searchCriteria.getActivityIds()));
						}

						if (Optional.ofNullable(searchCriteria.getRating()).isPresent()) {
							ratingPredicate = criteriaBuilder.and(root.get("rating").in(searchCriteria.getRating()));
						}

						

						if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
							themePredicate = criteriaBuilder
									.and(root.get("themeName").in(searchCriteria.getThemeNames()));
						}

						if (Optional.ofNullable(searchCriteria.getTagNames()).isPresent()) {
							tagPredicate = criteriaBuilder.and(root.get("tagName").in(searchCriteria.getTagNames()));
						}

						if (Optional.ofNullable(searchCriteria.getModeOfParticipations()).isPresent()) {
							modePredicate = criteriaBuilder
									.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
						}
						
						

						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), startDate);
						}
						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDatePredicate = criteriaBuilder.lessThan(root.get("createdOn"), endDate);
						}

						/*
						 * ================================
						 * 
						 */

						if (Optional.ofNullable(uploadedByAdminPredicate).isPresent()) {
							predicateList.add(uploadedByAdminPredicate);
						}

						if (Optional.ofNullable(locationPredicate).isPresent()) {
							predicateList.add(locationPredicate);
						}
						if (Optional.ofNullable(themePredicate).isPresent()) {
							predicateList.add(themePredicate);
						}

						if (Optional.ofNullable(modePredicate).isPresent()) {
							predicateList.add(modePredicate);
							combinedPredicate = criteriaBuilder.and(modePredicate);
						}
						if (Optional.ofNullable(tagPredicate).isPresent()) {
							predicateList.add(tagPredicate);
						}

						if (Optional.ofNullable(startDatePredicate).isPresent()) {
							predicateList.add(startDatePredicate);
						}

						if (Optional.ofNullable(endDatePredicate).isPresent()) {
							predicateList.add(endDatePredicate);
						}

						if (Optional.ofNullable(createdByPredicate).isPresent()) {
							predicateList.add(createdByPredicate);
						}

						if (Optional.ofNullable(employeeIdPredicate).isPresent()) {
							predicateList.add(employeeIdPredicate);
						}

						if (Optional.ofNullable(activityNamePredicate).isPresent()) {
							predicateList.add(activityNamePredicate);
						}

						if (Optional.ofNullable(ratingPredicate).isPresent()) {
							predicateList.add(ratingPredicate);
						}

						if (Optional.ofNullable(deletedByAdminPredicate).isPresent()) {
							predicateList.add(deletedByAdminPredicate);
						}
						
						if (Optional.ofNullable(publishOrUnpublishPredicate).isPresent()) {
							predicateList.add(publishOrUnpublishPredicate);
						}

						if (Optional.ofNullable(manualUploadPredicate).isPresent()) {
							predicateList.add(manualUploadPredicate);
						}
						
						combinedPredicate = criteriaBuilder
								.and(predicateList.toArray(new Predicate[predicateList.size()]));

						return combinedPredicate;
					} else {

						if (Optional.ofNullable(searchCriteria.getRole()).isPresent()) {
							if (searchCriteria.getRole().equals(Constants.ROLE_EMPLOYEE)) {
								createdByPredicate = criteriaBuilder.equal(root.get("createdBy"),
										searchCriteria.getUsername());
							}
						}

						combinedPredicate = criteriaBuilder
								.and(predicateList.toArray(new Predicate[predicateList.size()]));

						return combinedPredicate;

					}
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				return combinedPredicate;

			}

		};

	}


	public static Specification<ActivityPromotion> getActivityPromotionSpecification(SearchCriteria searchCriteria) {

		return new Specification<ActivityPromotion>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<ActivityPromotion> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				Queue<Predicate> predicateList = new LinkedList<>();

				Predicate themePredicate = null;
				Predicate tagPredicate = null;
				Predicate modePredicate = null;
				Predicate combinedPredicate = null;
				Predicate activityNamePredicate = null;
				Predicate locationPredicate = null;
				Predicate startDatePredicate = null;
				Predicate endDatePredicate = null;
				Predicate deletedPredicate = null;
				Predicate createdByPredicate = null;

				try {

					if (Optional.ofNullable(searchCriteria).isPresent() && !searchCriteria.isEmpty()) {

					
						 

						searchCriteria.setDeletedByAdmin(Constants.FALSE);

						LocalDateTime startDate = null;
						LocalDateTime endDate = null;

						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDate = CommonUtils.getActivityDateParam(searchCriteria.getStartDate());
						}

						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDate = CommonUtils.getActivityDateParam(searchCriteria.getEndDate());
							endDate = endDate.plusDays(1);
						}



						if (Optional.ofNullable(searchCriteria.getActivityIds()).isPresent()) {
							activityNamePredicate = criteriaBuilder
									.and(root.get("activityId").in(searchCriteria.getActivityIds()));
						}

						

						if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
							themePredicate = criteriaBuilder
									.and(root.get("promotionTheme").in(searchCriteria.getThemeNames()));
						}
						
						if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
							tagPredicate = criteriaBuilder
									.and(root.get("tagName").in(searchCriteria.getTagNames()));
						}
						
						if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
							modePredicate = criteriaBuilder
									.and(root.get("mode").in(searchCriteria.getModeOfParticipations()));
						}

						if (Optional.ofNullable(searchCriteria.getStartDate()).isPresent()) {
							startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
						}
						if (Optional.ofNullable(searchCriteria.getEndDate()).isPresent()) {
							endDatePredicate = criteriaBuilder.lessThan(root.get("endDate"), endDate);
						}

						/*
						 * ================================
						 * 
						 */

						if (Optional.ofNullable(locationPredicate).isPresent()) {
							predicateList.add(locationPredicate);
						}
						if (Optional.ofNullable(themePredicate).isPresent()) {
							predicateList.add(themePredicate);
						}
						if (Optional.ofNullable(tagPredicate).isPresent()) {
							predicateList.add(tagPredicate);
						}
						if (Optional.ofNullable(modePredicate).isPresent()) {
							predicateList.add(modePredicate);
						}

						if (Optional.ofNullable(startDatePredicate).isPresent()) {
							predicateList.add(startDatePredicate);
						}

						if (Optional.ofNullable(endDatePredicate).isPresent()) {
							predicateList.add(endDatePredicate);
						}

						if (Optional.ofNullable(createdByPredicate).isPresent()) {
							predicateList.add(createdByPredicate);
						}

						if (Optional.ofNullable(activityNamePredicate).isPresent()) {
							predicateList.add(activityNamePredicate);
						}

						if (Optional.ofNullable(deletedPredicate).isPresent()) {
							predicateList.add(deletedPredicate);
						}
																										
						combinedPredicate = criteriaBuilder
								.and(predicateList.toArray(new Predicate[predicateList.size()]));

						return combinedPredicate;
					} 
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				return combinedPredicate;

			}

		};

	}

}
