package com.seniormonitor.server.security;

import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.exception.ForbiddenException;

public final class RegionAccess {

    private RegionAccess() {
    }

    /** MASTER가 아니면서 아직 관할 지역(gu)을 배정받지 못한 담당자인지 여부 */
    public static boolean isUnassigned(CurrentManager manager) {
        return !manager.isMaster() && manager.gu() == null;
    }

    /** 목록 조회 시 사용할 gu 필터값. MASTER는 null(전체 조회) */
    public static String guFilter(CurrentManager manager) {
        return manager.isMaster() ? null : manager.gu();
    }

    /** 목록 조회 시 사용할 dong 필터값. MASTER는 null(전체 조회) */
    public static String dongFilter(CurrentManager manager) {
        return manager.isMaster() ? null : manager.dong();
    }

    /** manager가 null이면 로그인 없이 호출된 것(APK 등 공개 엔드포인트)이므로 지역 제한을 적용하지 않는다. */
    public static void assertAccessible(Senior senior, CurrentManager manager) {
        if (manager == null || manager.isMaster()) {
            return;
        }
        boolean guMatch = manager.gu() != null && manager.gu().equals(senior.getGu());
        boolean dongMatch = manager.dong() == null || manager.dong().equals(senior.getDong());
        if (!guMatch || !dongMatch) {
            throw new ForbiddenException("ERR_FORBIDDEN_REGION", "관할 지역 밖의 대상자입니다.");
        }
    }
}
