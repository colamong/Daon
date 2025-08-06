	package com.daon.be.pet.dto;

	import com.daon.be.pet.entity.UserPet;
	import com.daon.be.pet.service.UserPetService;

	public record UserPetStatusDto(
		String name,
		int currentStage,
		int totalExperience,
		int progressPercent,
		String imageUrl
	) {
		public static UserPetStatusDto from(UserPet pet, int conversationCount) {
			int stage = pet.getCurrentStage();
			int maxStage = pet.getPet().getMaxStage();
			int nextStage = Math.min(stage + 1, maxStage);

			int[] levelReq = {0, 2, 6, 12, 20, 32, 46}; // 누적 대화 수 기준

			// 레벨 진행 구간에 맞는 누적 요구치로 진행률 계산
			int prevReq = levelReq[stage - 1];
			int nextReq = levelReq[stage];

			int percent = (int)(((double)(conversationCount - prevReq) / (nextReq - prevReq)) * 100);

			if (percent >= 100) {
				// 레벨업 처리: 다음 레벨로 올라가고 진행률 0%로 초기화
				stage = nextStage;
				percent = 0;
			} else {
				percent = Math.max(0, percent);
			}

			String image = "/images/lv_" + stage + ".png";

			System.out.println("🧩 stage = " + stage);
			System.out.println("🧩 conversationCount = " + conversationCount);
			System.out.println("🧩 prev = " + prevReq);
			System.out.println("🧩 next = " + nextReq);
			System.out.println("🧩 percent = " + percent);

			return new UserPetStatusDto(pet.getName(), stage, pet.getExperience(), percent, image);
		}


	}
