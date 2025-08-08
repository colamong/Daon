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
		public static UserPetStatusDto fromEntity(UserPet up, int totalConversations) {
			int stage = up.getCurrentStage();
			int max = 7;

			int currMin = requiredTotalForStage(stage);
			int nextStage = Math.min(stage + 1, max);
			int nextMin = requiredTotalForStage(nextStage);

			int progressPercent;
			if (stage >= max) {
				progressPercent = 100;
			} else {
				int denom = Math.max(1, nextMin - currMin);
				int numer = Math.max(0, totalConversations - currMin);
				progressPercent = Math.min(100, (int) Math.floor((numer * 100.0) / denom));
			}

			String imageUrl = String.format("%s_%d.png", up.getPet().getImageBaseUrl(), stage);

			return new UserPetStatusDto(
				up.getName(),
				stage,
				up.getExperience(),
				progressPercent,
				imageUrl
			);
		}

		/** DTO에서도 동일 식을 쓰게 유틸로 복제하거나 공용 유틸 클래스로 분리 */
		private static int requiredTotalForStage(int stage) {
			if (stage <= 1) return 0;
			return stage * (stage - 1);
		}


	}
