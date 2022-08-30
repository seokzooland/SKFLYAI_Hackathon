import torch
from transformers import PreTrainedTokenizerFast, GPT2LMHeadModel

Q_TKN = "<usr>"
A_TKN = "<sys>"
BOS = '</s>'
EOS = '</s>'
MASK = '<unused0>'
SENT = '<unused1>'
PAD = '<pad>'

koGPT2_TOKENIZER = PreTrainedTokenizerFast.from_pretrained("skt/kogpt2-base-v2",
                                                           bos_token=BOS, eos_token=EOS, unk_token='<unk>',
                                                           pad_token=PAD, mask_token=MASK)

torch_model5 = GPT2LMHeadModel.from_pretrained('/home/szland/model5.pt')
tokenizer = PreTrainedTokenizerFast.from_pretrained("skt/kogpt2-base-v2", bos_token='</s>', eos_token='</s>',
                                                    unk_token='<unk>', pad_token='<pad>', mask_token='<mask>')


def chatbot_model5(text):
    with torch.no_grad():
        a = ""
        while 1:
            input_ids = torch.LongTensor(koGPT2_TOKENIZER.encode(Q_TKN + text + A_TKN + text)).unsqueeze(dim=0)
            pred = torch_model5(input_ids)
            pred = pred.logits
            gen = koGPT2_TOKENIZER.convert_ids_to_tokens(torch.argmax(pred, dim=-1).squeeze().numpy().tolist())[-1]
            if gen == EOS:
                break
            a += gen.replace("▁", " ")
            input_ids = tokenizer.encode(a)
            gen_ids = torch_model5.generate(torch.tensor([input_ids]),
                                            max_length=8,
                                            min_length=4,
                                            num_bins=1,
                                            repetition_penalty=2.0,
                                            pad_token_id=tokenizer.pad_token_id,
                                            eos_token_id=tokenizer.eos_token_id,
                                            bos_token_id=tokenizer.bos_token_id,
                                            use_cache=True)
            generated = tokenizer.decode(gen_ids[0, :].tolist())
        # print("Chatbot > {}".format(a.strip()))
        print("Chatbot > {}".format(generated.strip()))
        return a
