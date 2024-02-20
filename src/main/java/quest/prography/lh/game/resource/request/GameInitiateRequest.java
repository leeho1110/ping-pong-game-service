package quest.prography.lh.game.resource.request;

import quest.prography.lh.game.service.input.GameInitiateInput;

public record GameInitiateRequest(
        int seed,
        int quantity
) {

    public GameInitiateInput toInitiateInput() {
        return new GameInitiateInput(seed, quantity);
    }
}
